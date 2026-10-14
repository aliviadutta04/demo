import http from 'k6/http';
import { check, fail } from 'k6';
import { Trend } from 'k6/metrics';
import { buildSummary, login, performanceConfig, requestUrl, setting } from './common.js';

const createDuration = new Trend('employee_create_duration', true);
const maxLatency = Number(setting(
  'K6_EMPLOYEE_CREATE_P95_MS',
  performanceConfig.thresholds.employeeCreateP95Milliseconds,
));
const maxErrorRate = Number(setting('K6_MAX_ERROR_RATE', performanceConfig.thresholds.maximumErrorRate));
const minCheckRate = Number(setting('K6_MIN_CHECK_RATE', performanceConfig.thresholds.minimumCheckRate));

export const options = {
  vus: Number(setting('K6_VUS', performanceConfig.load.virtualUsers)),
  duration: setting('K6_DURATION', performanceConfig.load.duration),
  thresholds: {
    checks: [`rate>${minCheckRate}`],
    http_req_failed: [`rate<${maxErrorRate}`],
    employee_create_duration: [`p(95)<${maxLatency}`],
  },
};

export default function () {
  login();

  const employeePath = setting('K6_EMPLOYEE_PATH', performanceConfig.employee.path);
  const uniqueName = `K6${Date.now()}${__VU}${__ITER}`;
  const payloadTemplate = setting('K6_EMPLOYEE_PAYLOAD', JSON.stringify(performanceConfig.employee.payload));
  const payload = JSON.parse(payloadTemplate
    .replace(/\{\{firstName\}\}/g, uniqueName)
    .replace(/\{\{lastName\}\}/g, `Run${__VU}Iter${__ITER}`));
  const response = http.post(requestUrl(employeePath), JSON.stringify(payload), {
    headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
    tags: { operation: 'employee-create' },
  });
  createDuration.add(response.timings.duration);

  const expectedStatus = Number(setting(
    'K6_EMPLOYEE_CREATE_SUCCESS_STATUS',
    performanceConfig.employee.createSuccessStatus,
  ));
  const created = check(response, {
    'employee creation returns the configured success status': (result) => result.status === expectedStatus,
  });
  let responseBody = {};
  try {
    responseBody = response.json();
  } catch {
    responseBody = {};
  }

  const employeeNumber = responseBody?.data?.empNumber;
  if (employeeNumber !== undefined && employeeNumber !== null) {
    const acceptedStatuses = setting(
      'K6_EMPLOYEE_DELETE_SUCCESS_STATUSES',
      performanceConfig.employee.deleteSuccessStatuses.join(','),
    )
      .split(',')
      .map((status) => Number(status.trim()));
    const deleteResponse = http.del(requestUrl(`${employeePath}/${employeeNumber}`), null, {
      headers: { Accept: 'application/json' },
      tags: { operation: 'employee-cleanup' },
    });
    const deleted = check(deleteResponse, {
      'created employee is removed after the measurement': (result) => acceptedStatuses.includes(result.status),
    });
    if (!deleted) {
      fail(`Employee cleanup returned HTTP ${deleteResponse.status}`);
    }
  } else if (created) {
    fail('Employee API returned success without data.empNumber; cleanup is not possible');
  }

  if (!created) {
    fail(`Employee creation returned HTTP ${response.status}; expected HTTP ${expectedStatus}`);
  }
}

export function handleSummary(data) {
  return buildSummary(data, 'employee-create');
}
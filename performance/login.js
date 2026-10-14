import { Trend } from 'k6/metrics';
import { buildSummary, login, performanceConfig, setting } from './common.js';

const loginDuration = new Trend('login_duration', true);
const maxLatency = Number(setting('K6_LOGIN_P95_MS', performanceConfig.thresholds.loginP95Milliseconds));
const maxErrorRate = Number(setting('K6_MAX_ERROR_RATE', performanceConfig.thresholds.maximumErrorRate));
const minCheckRate = Number(setting('K6_MIN_CHECK_RATE', performanceConfig.thresholds.minimumCheckRate));

export const options = {
  vus: Number(setting('K6_VUS', performanceConfig.load.virtualUsers)),
  duration: setting('K6_DURATION', performanceConfig.load.duration),
  thresholds: {
    checks: [`rate>${minCheckRate}`],
    http_req_failed: [`rate<${maxErrorRate}`],
    login_duration: [`p(95)<${maxLatency}`],
  },
};

export default function () {
  const response = login();
  loginDuration.add(response.timings.duration);
}

export function handleSummary(data) {
  return buildSummary(data, 'login');
}
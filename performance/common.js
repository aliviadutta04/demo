import http from 'k6/http';
import { check, fail } from 'k6';

export const performanceConfig = JSON.parse(open('./performance/config.json'));

export function setting(name, fallback) {
  const value = __ENV[name];
  return value && !value.startsWith('$(') ? value : fallback;
}

export function required(name) {
  const value = __ENV[name];
  if (!value || value.startsWith('$(')) {
    throw new Error(`Required environment variable is missing: ${name}`);
  }
  return value;
}

export function requestUrl(path) {
  const baseUrl = required('K6_BASE_URL').replace(/\/+$/, '');
  return `${baseUrl}/${path.replace(/^\/+/, '')}`;
}

export function login() {
  const username = required('ORANGEHRM_USERNAME');
  const password = required('ORANGEHRM_PASSWORD');
  const payloadTemplate = setting('K6_LOGIN_PAYLOAD', JSON.stringify(performanceConfig.login.payload));
  const payload = payloadTemplate
    .replace(/\{\{username\}\}/g, username)
    .replace(/\{\{password\}\}/g, password);
  const response = http.post(
    requestUrl(setting('K6_LOGIN_PATH', performanceConfig.login.path)),
    payload,
    {
      headers: {
        Accept: 'application/json',
        'Content-Type': setting('K6_LOGIN_CONTENT_TYPE', performanceConfig.login.contentType),
      },
      tags: { operation: 'login' },
    },
  );
  const expectedStatus = Number(setting('K6_LOGIN_SUCCESS_STATUS', performanceConfig.login.successStatus));
  const accepted = check(response, {
    'login returns the configured success status': (result) => result.status === expectedStatus,
  });
  if (!accepted) {
    fail(`Login returned HTTP ${response.status}; expected HTTP ${expectedStatus}`);
  }
  return response;
}

export function buildSummary(data, reportName) {
  const configuredDirectory = setting('K6_REPORT_DIR', performanceConfig.reports.directory);
  const reportDirectory = configuredDirectory.replace(/\/+$/, '') || '.';
  const metrics = Object.entries(data.metrics).map(([name, metric]) => {
    const values = Object.entries(metric.values || {})
      .map(([key, value]) => `${key}: ${formatValue(value)}`)
      .join('<br>');
    return `<tr><td>${escapeHtml(name)}</td><td>${values}</td></tr>`;
  }).join('');
  const html = `<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${escapeHtml(reportName)} performance report</title>
  <style>
    body { font: 15px/1.5 system-ui, sans-serif; margin: 2rem auto; max-width: 960px; padding: 0 1rem; color: #18212b; }
    h1 { font-size: 1.6rem; }
    table { border-collapse: collapse; width: 100%; }
    th, td { border-bottom: 1px solid #d6dce2; padding: .65rem; text-align: left; vertical-align: top; }
    th { background: #f1f4f7; }
    code { color: #215c46; }
  </style>
</head>
<body>
  <h1>${escapeHtml(reportName)} performance report</h1>
  <p>Generated: <code>${new Date().toISOString()}</code></p>
  <table>
    <thead><tr><th>Metric</th><th>Summary</th></tr></thead>
    <tbody>${metrics}</tbody>
  </table>
</body>
</html>`;

  return {
    [`${reportDirectory}/${reportName}-performance.html`]: html,
    [`${reportDirectory}/${reportName}-performance.json`]: JSON.stringify(data, null, 2),
  };
}

function formatValue(value) {
  return typeof value === 'number' ? value.toFixed(2) : String(value);
}

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, (character) => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;',
  })[character]);
}
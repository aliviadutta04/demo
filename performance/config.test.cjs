const assert = require('node:assert/strict');
const { readFileSync } = require('node:fs');
const { join } = require('node:path');
const { test } = require('node:test');

const configuration = JSON.parse(readFileSync(join(__dirname, 'config.json'), 'utf8'));

test('load profile has a positive virtual-user count and a duration', () => {
  assert.ok(Number.isInteger(configuration.load.virtualUsers));
  assert.ok(configuration.load.virtualUsers > 0);
  assert.match(configuration.load.duration, /^\d+[smh]$/);
});

test('latency and request-rate thresholds are valid', () => {
  assert.ok(configuration.thresholds.maximumErrorRate >= 0);
  assert.ok(configuration.thresholds.maximumErrorRate < 1);
  assert.ok(configuration.thresholds.minimumCheckRate > 0);
  assert.ok(configuration.thresholds.minimumCheckRate <= 1);
  assert.ok(configuration.thresholds.loginP95Milliseconds > 0);
  assert.ok(configuration.thresholds.employeeCreateP95Milliseconds > 0);
});

test('login and employee API contracts contain usable paths and statuses', () => {
  assert.match(configuration.login.path, /^\//);
  assert.ok(Number.isInteger(configuration.login.successStatus));
  assert.match(configuration.employee.path, /^\//);
  assert.ok(Number.isInteger(configuration.employee.createSuccessStatus));
  assert.ok(configuration.employee.deleteSuccessStatuses.length > 0);
  assert.ok(configuration.employee.deleteSuccessStatuses.every(Number.isInteger));
  assert.ok(configuration.employee.payload.firstName.includes('{{firstName}}'));
  assert.ok(configuration.employee.payload.lastName.includes('{{lastName}}'));
});
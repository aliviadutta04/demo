# OrangeHRM Selenium BDD Automation Framework
## Overview

This Maven framework uses Selenium, Cucumber, TestNG, REST Assured, and Java. It covers employee create/update/read/delete flows, API response verification, Admin and ESS navigation checks, failure screenshots, HTML reports, and optional video recording.

## Requirements

- Java 17 or newer
- Maven 3.8 or newer
- Chrome, Firefox, or Edge installed locally (Selenium Manager resolves the matching driver)
- FFmpeg on `PATH` for inline video playback in Cucumber HTML reports
- OrangeHRM application credentials with permission to create and delete employees
- A separately provisioned ESS account for the `@role` scenario

The `dev` and `qa` profiles currently target the public OrangeHRM demo site. It is shared and may be reset or rate-limited. `prod` intentionally has no checked-in URL; provide its endpoint through environment variables or `-D` properties.

## Configuration and Secrets

The environment is selected with `-Denv=qa`, `ORANGEHRM_ENV=qa`, or the `environment` property in `src/test/resources/config/config.properties`. The main Azure pipeline exposes an environment parameter, and the regression pipeline sets `qa`. Shared settings, including browser arguments, viewport, timeouts, retry policy, output paths, and video conversion, are in `config.properties`; endpoints are in `config-{environment}.properties`. System properties override environment variables, which override `.env` values and profile properties. Dotted setting names map to underscore-separated environment variables, for example `browser.width` to `ORANGEHRM_BROWSER_WIDTH`.

Set credentials outside the repository:

| Variable | Purpose |
| --- | --- |
| `ORANGEHRM_USERNAME` | Admin test account |
| `ORANGEHRM_PASSWORD` | Admin test password |
| `ORANGEHRM_ESS_USERNAME` | Provisioned ESS test account |
| `ORANGEHRM_ESS_PASSWORD` | ESS test password |
| `ORANGEHRM_BASE_URL` | Optional UI URL override |
| `ORANGEHRM_API_BASE_URL` | Optional API URL override |
| `ORANGEHRM_ENV` | Environment profile override (`dev`, `qa`, or `prod`) |

For local runs, copy `.env.example` to `.env` and enter your rotated test credentials there. `.env` is ignored by Git and is loaded by the TestNG runner. For Azure DevOps, define `ORANGEHRM_USERNAME`, `ORANGEHRM_PASSWORD`, `ORANGEHRM_ESS_USERNAME`, and `ORANGEHRM_ESS_PASSWORD` as secret variables in the pipeline or its variable group. The YAML passes them as process environment variables; CI therefore does not need or read a `.env` file. Admin secrets are validated before tests start, and ESS secrets are required only when the selected tag is `@role`. Credentials previously placed in properties files should be rotated before reuse.

## Run Tests

From the workspace root, create the ignored `.env` file once, fill in the credentials, and run:

```powershell
Copy-Item demo/.env.example demo/.env
notepad demo/.env
mvn -f demo/pom.xml test -Dtest=CucumberTest -Denv=dev -Dheadless=true "-Dcucumber.filter.tags=@smoke"
```

Chrome is the default browser. Select another installed browser with `-Dbrowser=firefox` or `-Dbrowser=edge`; supported values are `chrome`, `firefox`, and `edge`.

If admin credentials are absent, setup stops before Chrome starts and the configuration failure is not retried. The same credentials can be passed as `-Dusername=... -Dpassword=...` when needed.

Common tag filters include `@smoke`, `@negative`, `@dataDriven`, `@role`, and `@loginNegative`. The negative-login scenario uses generated nonexistent credentials and can run without Admin secrets:

```powershell
mvn -f demo/pom.xml test -Dtest=CucumberTest "-Dcucumber.filter.tags=@loginNegative"
```

Run the framework-only tests without launching a browser, then validate the k6 settings with Node:

```powershell
mvn -f demo/pom.xml "-Dtest=ConfigReaderTest,RetryAnalyzerTest" test
node --test demo/performance/config.test.cjs
```

`CucumberTest` is the single runner for local and CI execution. Runner feature, glue, and report settings are in `src/test/resources/cucumber.properties`; Cucumber tag expressions select scenarios with `-Dcucumber.filter.tags`. The runner permits concurrent scenarios only when they carry `@parallel`; all other scenarios take an exclusive execution lock. Up to three `@parallel` scenarios run concurrently by default; set `-Dcucumber.threads=1` to disable concurrency. The role suite checks the configured Admin account's Admin/PIM access and the configured ESS account's My Info access and lack of Admin/PIM access.

`SmokeCucumberRunner` is the fixed parallel suite for the `@smoke` and `@negative` scenarios. It runs those selected scenarios concurrently without requiring `@parallel` on their feature declarations:

```powershell
mvn -f demo/pom.xml "-Dtest=SmokeCucumberRunner" -Dheadless=true test
```

The Azure pipelines run the same runner with Xvfb, non-headless Chrome, Maven dependency caching, and publish Surefire/Cucumber reports, screenshots, and recordings. CI excludes scenarios tagged `@quarantine`.

## Test Design and Reliability

- PicoContainer shares a scenario-scoped `TestContext`; WebDriver instances are thread-local.
- Employee names are suffixed with a random run identifier to avoid collisions.
- The `@After` hook attempts API deletion for employees still tracked in the scenario, including after a failed step. Explicit UI deletion clears the cleanup marker.
- The API check verifies employee number, first name, and updated last name. The UI update check waits for the saved last-name value.
- TestNG `RetryAnalyzer` retries a scenario at most twice by default. Set `-Dretry.maxAttempts=0` to disable retries or choose another limit. Retry attempts are logged; a retry does not turn a flaky test into a passing signal for triage purposes.
- Every failed attempt, including an exhausted retry, is recorded with timestamp, scenario, attempt, and outcome in `target/flaky-tests/retry-events.csv`; Azure publishes this as the `Flaky-Test-Retry-Events` artifact. Review that report with Surefire results each build and track repeat offenders by scenario and issue.
- Quarantine only reproducibly flaky scenarios: add `@quarantine` to the scenario, record its owner and tracking issue, and keep it excluded from normal CI until fixed. Remove the tag after the fix and verify it in CI. No scenario should be quarantined without a linked issue; do not increase retries to mask product defects.
- Each Cucumber scenario attaches an H.264 MP4 recording to the Cucumber HTML report and preserves the original AVI and converted MP4 under `target/videos`. Local video capture requires a graphical display and FFmpeg on `PATH`; if conversion is unavailable, the report offers the AVI as a download instead of embedding an unplayable video. Azure installs FFmpeg, uses Xvfb, and publishes both formats as build artifacts.

## Reports and Outputs

- Cucumber HTML: `target/cucumber-report`
- Surefire XML/text: `target/surefire-reports`
- Failure screenshots: `target/screenshots`
- Video recordings: `target/videos`
- Flaky retry events: `target/flaky-tests/retry-events.csv`
- k6 performance reports: `target/performance-results` (Azure artifact: `K6-Performance-Reports`)

These are generated outputs and should not be committed.

## Performance Testing

Optional k6 scripts are in `performance/`; their defaults are in `performance/config.json`. The login scenario measures the configured login endpoint; the employee-create scenario authenticates per virtual user, creates uniquely named employees, and deletes each created employee after recording the create latency. Configure `K6_BASE_URL` to the API origin/base path and verify the login endpoint, payload, content type, expected statuses, and employee response shape for the target OrangeHRM version. The employee-create API must return `data.empNumber` so cleanup can run; successful creates without that identifier fail the iteration and require manual cleanup.

Install k6 from [k6.io](https://grafana.com/docs/k6/latest/set-up/install-k6/) and set the relevant values from `.env.example` in your shell. For example, in PowerShell:

```powershell
Push-Location demo
$env:ORANGEHRM_USERNAME = 'your-admin-test-user'
$env:ORANGEHRM_PASSWORD = 'your-admin-test-password'
$env:K6_BASE_URL = 'https://your-host/web/index.php'
$env:K6_VUS = '2'
$env:K6_DURATION = '30s'
New-Item -ItemType Directory -Force performance-results | Out-Null
k6 run performance/login.js
k6 run performance/employee-create.js
Pop-Location
```

Both scripts enforce check-rate, HTTP error-rate, and p95 latency thresholds. They generate HTML and JSON summaries under `performance-results` by default; set `K6_REPORT_DIR` to change the destination. The Azure pipeline exposes `runPerformance`, defaulting to `false`; enable it only against an approved test environment, set the `K6_BASE_URL` pipeline variable, and optionally define the documented `K6_*` pipeline variables to override load, endpoint, payload, and threshold defaults. Credentials are supplied through the existing Admin secret variables. Performance reports publish as a separate build artifact.

## Current Role-Test Requirement

The role suite verifies both configured Admin and ESS navigation permissions. The public demo does not provide a stable, documented account-provisioning API contract, so account creation is intentionally external; provision the ESS user and configure its credentials as secret variables before running `@role`.
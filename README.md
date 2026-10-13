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

The `ENVIRONMENT` constant in `CucumberTest.java` (`dev`, `qa`, or `prod`) is the runner's default. Override it with `-Denv=qa` when invoking Maven; an explicit system property takes precedence. The main Azure pipeline exposes an environment parameter, and the regression pipeline sets `qa`. Shared browser settings are in `src/test/resources/config/config.properties`; endpoints are in `config-{environment}.properties`. For other settings, system properties override environment variables, which override profile properties.

Set credentials outside the repository:

| Variable | Purpose |
| --- | --- |
| `ORANGEHRM_USERNAME` | Admin test account |
| `ORANGEHRM_PASSWORD` | Admin test password |
| `ORANGEHRM_ESS_USERNAME` | Provisioned ESS test account |
| `ORANGEHRM_ESS_PASSWORD` | ESS test password |
| `ORANGEHRM_BASE_URL` | Optional UI URL override |
| `ORANGEHRM_API_BASE_URL` | Optional API URL override |

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

`CucumberTest` is the single runner for local and CI execution. Cucumber tag expressions select scenarios with `-Dcucumber.filter.tags`. Its scenario data provider runs three threads by default; set `-Dcucumber.threads=1` for serial execution or raise it to increase parallelism.

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

These are generated outputs and should not be committed.

## Current Role-Test Requirement

The role scenario verifies that a provisioned ESS account sees My Info and does not see Admin or PIM. The public demo does not provide a stable, documented account-provisioning API contract, so account creation is intentionally external; provision the ESS user and configure its credentials as secret variables before running `@role`.
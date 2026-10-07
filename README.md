# OrangeHRM Selenium BDD Automation Framework

## 1. Project Overview

This project is an end-to-end automation framework for the OrangeHRM
application.

The framework covers:

- Authentication
- Employee creation
- Employee update
- Role validation
- API-level employee verification
- Employee deletion
- Performance testing of APIs
- CI/CD execution using Azure DevOps
- HTML reporting
- Failure screenshots

---

## 2. Technology Stack

- Java 21
- Selenium WebDriver
- Cucumber BDD
- JUnit 4
- REST Assured
- Gson
- Maven
- Azure DevOps
- Git/GitHub

---

## 3. Framework Structure

```text
orangehrmdemo
├── src
│   └── test
│       ├── java
│       │   └── orangehrmpoc
│       │       ├── api
│       │       ├── config
│       │       ├── driver
│       │       ├── hooks
│       │       ├── pages
│       │       ├── runner
│       │       ├── steps
│       │       └── utils
│       │
│       └── resources
│           ├── config
│           ├── features
│           └── testdata
│
├── performance
│   ├── login-api.js
│   ├── employee-create-api.js
│   └── config.js
│
├── azure-pipelines.yml
├── pom.xml
└── README.md
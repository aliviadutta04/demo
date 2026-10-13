@role
Feature: Role-based navigation

  Scenario: ESS user cannot access administrative navigation
    Given I open the employee application
    And I login with valid credentials
    And I am logged in as the configured ESS user
    Then the ESS user should see only ESS navigation
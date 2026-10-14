@role
Feature: Role-based navigation

  Scenario: Admin user can access administrative navigation
    Given I open the employee application
    And I login with valid credentials
    Then the configured Admin user should see Admin and PIM navigation

  Scenario: ESS user cannot access administrative navigation
    Given I open the employee application
    And I login with valid credentials
    And I am logged in as the configured ESS user
    Then the ESS user should see only ESS navigation
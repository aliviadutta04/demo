@employee
Feature: Employee lifecycle

  Background:
    Given I open the employee application
    And I login with valid credentials

  @smoke
  Scenario: Complete employee lifecycle
    When I create a new employee using "employee1" data
    Then the employee should be created successfully
    And the employee role should be validated
    When I update the employee details
    Then the employee details should be updated successfully
    And the employee should be verified through API
    When I delete the employee
    Then the employee should be deleted successfully

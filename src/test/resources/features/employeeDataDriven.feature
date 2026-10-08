@dataDriven
Feature: Employee lifecycle using multiple employee data

  Background:
    Given I open the employee application
    And I login with valid credentials

  @smokeDataDriven
  Scenario Outline: Complete employee lifecycle for multiple employees
    When I create a new employee using "<testCase>" data
    Then the employee should be created successfully
    And the employee role should be validated
    When I update the employee details
    Then the employee details should be updated successfully

    Examples:
      | testCase  |
      | employee1 |
      | employee2 |
      | employee3 |

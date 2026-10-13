@deleteEmployee
Feature: Delete Employee

  Scenario: Delete employee after creation
    Given I open the employee application
    And I login with valid credentials
    When I create a new employee using "employee1" data
    Then the employee should be created successfully
    When I delete the employee
    Then the employee should be deleted successfully
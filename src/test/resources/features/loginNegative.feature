@negative @loginNegative
Feature: Negative login

  Scenario: Reject unknown user credentials
    Given I open the employee application
    When I submit invalid login credentials
    Then the invalid login error should be displayed
    And the login form should remain displayed
@web @auth
Feature: Login
  As a Resonance user
  I want to log in to the application
  So that I can access the available features

  # ${usernameOrEmailResonance} and ${passwordResonance} are resolved at runtime
  # from config.properties via ConfigReader.resolve() — credentials are never hardcoded here.

  Background:
    Given I am on the login page

  @positive
  Scenario: Login succeeds with valid credentials
    When I enter username "${usernameOrEmailResonance}"
    And  I enter password "${passwordResonance}"
    And  I click the Login button
    Then I should be redirected away from the login page
    And  I should be on the dashboard page

  @negative
  Scenario: Login fails with unregistered email
    When I enter username "unregistered_user@fake.com"
    And  I enter password "password"
    And  I click the Login button
    Then I should see an error or stay on the login page

  @negative
  Scenario: Login fails with wrong password
    When I enter username "${usernameOrEmailResonance}"
    And  I enter password "wrongpassword123"
    And  I click the Login button
    Then I should see an error or stay on the login page

  @negative
  Scenario: Login fails when username is empty
    When I enter username ""
    And  I enter password "password"
    And  I click the Login button
    Then I should stay on the login page

  @negative
  Scenario: Login fails when password is empty
    When I enter username "${usernameOrEmailResonance}"
    And  I enter password ""
    And  I click the Login button
    Then I should stay on the login page

  @negative
  Scenario Outline: Login fails with various invalid credential combinations
    When I enter username "<username>"
    And  I enter password "<password>"
    And  I click the Login button
    Then I should see an error or stay on the login page

    Examples:
      | username                      | password      |
      | unknown_user@fake.com         | password      |
      | ${usernameOrEmailResonance}   | wrongpassword |
      | ${usernameOrEmailResonance}   | ab            |

  @positive
  Scenario: Login page displays Register and Forgot Password links
    Then I should see a link to the register page
    And  I should see a forgot password link

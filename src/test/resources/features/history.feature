@web @history
Feature: Ticket History
  As a logged-in user
  I want to view my ticket history
  So that I can track tickets that are resolved and pending

  # Credentials use ${configKey} placeholders resolved from config.properties at runtime.

  Background:
    Given I am logged in as "${usernameOrEmailResonance}" with password "${passwordResonance}"

  @positive
  Scenario: Navigate to the ticket history page
    When I am on the dashboard page
    And  I click History in the navbar
    Then I should be on the history page

  @positive
  Scenario: History page loads successfully
    Given I am on the history page
    Then  the history page should be loaded successfully
    And   the history page content should not be empty

  @positive
  Scenario: Navigate back to the dashboard from the history page
    Given I am on the history page
    When  I click the back to Dashboard button
    Then  I should be on the dashboard page

  @negative
  Scenario: Accessing the history page without login should redirect to login
    Given I am not logged in
    When  I access the history page directly
    Then  I should be redirected to the login page

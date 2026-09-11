@web @ticket
Feature: View Ticket List
  As a logged-in user
  I want to view the list of available tickets
  So that I can monitor ticket status and progress

  # Credentials use ${configKey} placeholders resolved from config.properties at runtime.

  Background:
    Given I am logged in as "${usernameOrEmailResonance}" with password "${passwordResonance}"

  @positive
  Scenario: Dashboard displays the ticket list
    When I am on the dashboard page
    Then the dashboard should be loaded successfully
    And  the page content is not empty

  @positive
  Scenario: Filter tickets by newest order
    When I am on the dashboard page
    And  I filter tickets by order "Newest"
    Then the dashboard should be loaded successfully

  @positive
  Scenario: Filter tickets by vote count
    When I am on the dashboard page
    And  I filter tickets by order "Vote"
    Then the dashboard should be loaded successfully

  @positive
  Scenario: Search tickets by keyword
    When I am on the dashboard page
    And  I search for tickets with keyword "Test"
    Then the dashboard should be loaded successfully

  @negative
  Scenario: Accessing a ticket page with an invalid ID
    When I access the ticket page with ID "id-tidak-valid-xyz-123"
    Then the page shows an error or redirects

@web @ticket
Feature: Create New Ticket
  As a logged-in user
  I want to create a new ticket
  So that my issue or request can be handled

  # Credentials use ${configKey} placeholders resolved from config.properties at runtime.

  Background:
    Given I am logged in as "${usernameOrEmailResonance}" with password "${passwordResonance}"
    And   I am on the dashboard page

  @positive
  Scenario: Create a public ticket with complete data
    When I click the Create Ticket button
    Then I should be on the new ticket page
    When I enter the ticket title "Public Automation Ticket"
    And  I enter the ticket description "This is a ticket description created via automation"
    And  I click Submit Ticket
    Then the ticket should be created successfully

  @positive
  Scenario: Create a private ticket with complete data
    When I click the Create Ticket button
    Then I should be on the new ticket page
    When I enter the ticket title "Private Automation Ticket"
    And  I enter the ticket description "This is a private ticket description"
    And  I select the Private option
    And  I click Submit Ticket
    Then the ticket should be created successfully

  @negative
  Scenario: Creating a ticket without a title should fail
    When I click the Create Ticket button
    Then I should be on the new ticket page
    When I enter the ticket description "Description is provided but title is empty"
    And  I click Submit Ticket
    Then ticket creation should fail and I should stay on the new ticket page

  @negative
  Scenario: Accessing the new ticket page without login should redirect to login
    Given I am not logged in
    When  I access the new ticket page directly
    Then  I should be redirected to the login page

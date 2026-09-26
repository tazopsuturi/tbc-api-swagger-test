@pet @positive
Feature: Pet CRUD operations
  As a Petstore client
  I want to create, read, update and delete pets
  So that the store's pet catalogue stays accurate

  @smoke @create
  Scenario: Create a new pet
    Given a pet named "Rex" in category "Dogs" with status "available"
    When I create the pet
    Then the response status code should be 200
    And the response headers should include:
      | Content-Type                | application/json |
      | Access-Control-Allow-Origin | *                |
    And the response should match the "pet" JSON schema
    And the response body should match the pet details
    And the response time should be less than 10000 ms

  @create
  Scenario Outline: Create pets with each supported status
    Given a pet named "<name>" in category "<category>" with status "<status>"
    When I create the pet
    Then the response status code should be 200
    And the response should match the "pet" JSON schema
    And the response body should match the pet details

    Examples:
      | name    | category | status    |
      | Buddy   | Dogs     | available |
      | Tom     | Cats     | pending   |
      | Nemo    | Fish     | sold      |

  @smoke @read
  Scenario: Retrieve an existing pet by id
    Given an existing pet named "Whiskers" in category "Cats" with status "available"
    When I retrieve the pet
    Then the response status code should be 200
    And the response header "Content-Type" should contain "application/json"
    And the response should match the "pet" JSON schema
    And the response body should match the pet details

  @read
  Scenario: Find pets by status
    Given an existing pet named "Polly" in category "Birds" with status "pending"
    When I search for pets with status "pending" until the pet is listed
    Then the response status code should be 200
    And the response should match the "pet-list" JSON schema
    And every pet in the response should have status "pending"
    And the search results should include the pet

  @smoke @update
  Scenario: Update an existing pet with a JSON body
    Given an existing pet named "Max" in category "Dogs" with status "available"
    When I update the pet with name "Max the Great" and status "sold"
    Then the response status code should be 200
    And the response should match the "pet" JSON schema
    And the response body should match the pet details
    And retrieving the pet should return name "Max the Great" and status "sold"

  @update
  Scenario: Update an existing pet with form data
    Given an existing pet named "Luna" in category "Cats" with status "available"
    When I update the pet via form data with name "Luna Star" and status "pending"
    Then the response status code should be 200
    And the response should match the "api-response" JSON schema
    And the response message should be the pet id
    And retrieving the pet should return name "Luna Star" and status "pending"

  @smoke @delete
  Scenario: Delete an existing pet
    Given an existing pet named "Goldie" in category "Fish" with status "sold"
    When I delete the pet
    Then the response status code should be 200
    And the response should match the "api-response" JSON schema
    And the response message should be the pet id
    And the pet should no longer exist

  @e2e
  Scenario: Full pet lifecycle - create, read, update, delete
    Given a pet named "Charlie" in category "Dogs" with status "available"
    When I create the pet
    Then the response status code should be 200
    And the response body should match the pet details
    When I retrieve the pet
    Then the response status code should be 200
    And the response body should match the pet details
    When I update the pet with name "Charlie Brown" and status "pending"
    Then the response status code should be 200
    And retrieving the pet should return name "Charlie Brown" and status "pending"
    When I delete the pet
    Then the response status code should be 200
    And the response message should be the pet id
    And the pet should no longer exist

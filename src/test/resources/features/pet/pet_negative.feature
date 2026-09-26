@pet @negative
Feature: Pet API error handling
  As a Petstore client
  I want invalid pet requests to be rejected with meaningful errors
  So that I can detect and handle mistakes

  @read
  Scenario: Retrieve a pet that does not exist
    Given a pet id that does not exist
    When I retrieve the pet
    Then the response status code should be 404
    And the response header "Content-Type" should contain "application/json"
    And the response should match the "api-response" JSON schema
    And the response should be an API error with code 1, type "error" and message "Pet not found"

  @read
  Scenario Outline: Retrieve a pet with a non-numeric id "<id>"
    When I retrieve the pet with id "<id>"
    Then the response status code should be 404
    And the response should match the "api-response" JSON schema
    And the response error message should contain "NumberFormatException"

    Examples:
      | id    |
      | abc   |
      | 12abc |

  @create
  Scenario: Create a pet with a malformed JSON body
    When I send a create pet request with body "{\"id\": 1, \"name\": " and content type "application/json"
    Then the response status code should be 400
    And the response should match the "api-response" JSON schema
    And the response should be an API error with code 400, type "unknown" and message "bad input"

  @create
  Scenario: Create a pet with an unsupported content type
    When I send a create pet request with body "name=Rex" and content type "text/plain"
    Then the response status code should be 415

  @update
  Scenario: Update a pet with a malformed JSON body
    When I send an update pet request with body "not json at all" and content type "application/json"
    Then the response status code should be 400
    And the response should be an API error with code 400, type "unknown" and message "bad input"

  @update
  Scenario: Update a pet that does not exist using form data
    Given a pet id that does not exist
    When I update the pet via form data with name "Nobody" and status "sold"
    Then the response status code should be 404
    And the response should be an API error with code 404, type "unknown" and message "not found"

  @delete
  Scenario: Delete a pet that does not exist
    Given a pet id that does not exist
    When I delete the pet
    Then the response status code should be 404
    And the response body should be empty

  @delete
  Scenario: Delete a pet with a non-numeric id
    When I delete the pet with id "abc"
    Then the response status code should be 404
    And the response error message should contain "NumberFormatException"

  @delete
  Scenario: A deleted pet cannot be retrieved or deleted again
    Given an existing pet named "Shadow" in category "Cats" with status "available"
    When I delete the pet
    Then the response status code should be 200
    And the pet should no longer exist
    And the response should be an API error with code 1, type "error" and message "Pet not found"
    When I delete the pet
    Then the response status code should be 404

@store
Feature: Store orders
  As a Petstore customer
  I want to place, view and cancel orders for pets

  Background:
    Given an existing pet named "Bella" in category "Dogs" with status "available"

  @positive @smoke @create
  Scenario: Place an order for a pet
    Given an order for 2 units of the pet with status "placed"
    When I place the order
    Then the response status code should be 200
    And the response header "Content-Type" should contain "application/json"
    And the response should match the "order" JSON schema
    And the response body should match the order details

  @positive @read
  Scenario: Retrieve an existing order
    Given an existing order for 1 unit of the pet with status "approved"
    When I retrieve the order
    Then the response status code should be 200
    And the response should match the "order" JSON schema
    And the response body should match the order details

  @positive @delete
  Scenario: Delete an existing order
    Given an existing order for 3 units of the pet with status "placed"
    When I delete the order
    Then the response status code should be 200
    And the response should match the "api-response" JSON schema
    And the response message should be the order id
    And the order should no longer exist

  @negative @read
  Scenario: Retrieve an order that does not exist
    Given an order id that does not exist
    When I retrieve the order
    Then the response status code should be 404
    And the response should be an API error with code 1, type "error" and message "Order not found"

  @negative @read
  Scenario: Retrieve an order with a non-numeric id
    When I retrieve the order with id "abc"
    Then the response status code should be 404
    And the response error message should contain "NumberFormatException"

  @negative @delete
  Scenario: Delete an order that does not exist
    Given an order id that does not exist
    When I delete the order
    Then the response status code should be 404
    And the response should be an API error with code 404, type "unknown" and message "Order Not Found"

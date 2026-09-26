package org.example.tbc_swagger_api_project.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.example.tbc_swagger_api_project.client.StoreClient;
import org.example.tbc_swagger_api_project.context.TestContext;
import org.example.tbc_swagger_api_project.data.TestDataFactory;
import org.example.tbc_swagger_api_project.models.ApiResponse;
import org.example.tbc_swagger_api_project.models.Order;

public class StoreSteps {

    private final TestContext context;
    private final StoreClient storeClient;

    public StoreSteps(TestContext context, StoreClient storeClient) {
        this.context = context;
        this.storeClient = storeClient;
    }

    @Given("an order for {int} unit(s) of the pet with status {string}")
    public void prepareOrderPayload(int quantity, String status) {
        context.setOrder(TestDataFactory.order(context.pet().getId(), quantity, status));
    }

    @Given("an existing order for {int} unit(s) of the pet with status {string}")
    public void createExistingOrder(int quantity, String status) {
        prepareOrderPayload(quantity, status);
        Order order = context.order();

        Response placed = storeClient.placeOrder(order);
        Assertions.assertThat(placed.statusCode()).as("Precondition: place order, body: %s", placed.asString()).isEqualTo(200);
        context.trackOrder(order.getId());

        Response fetched = storeClient.getOrderUntil(order.getId(), r -> r.statusCode() == 200);
        Assertions.assertThat(fetched.statusCode()).as("Precondition: order %d is retrievable", order.getId()).isEqualTo(200);
    }

    @Given("an order id that does not exist")
    public void prepareNonExistentOrderId() {
        Order order = TestDataFactory.order(TestDataFactory.uniqueId(), 1, "placed");
        storeClient.deleteOrder(order.getId());
        Response fetched = storeClient.getOrderUntil(order.getId(), r -> r.statusCode() == 404);
        Assertions.assertThat(fetched.statusCode()).as("Precondition: order %d is absent", order.getId()).isEqualTo(404);
        context.setOrder(order);
    }

    @When("I place the order")
    public void placeOrder() {
        context.setResponse(storeClient.placeOrder(context.order()));
        context.trackOrder(context.order().getId());
    }

    @When("I retrieve the order")
    public void getCurrentOrder() {
        Long id = context.order().getId();
        context.setResponse(context.isTrackedOrder(id)
                ? storeClient.getOrderUntil(id, r -> r.statusCode() == 200)
                : storeClient.getOrder(id));
    }

    @When("I retrieve the order with id {string}")
    public void getOrderByRawId(String rawId) {
        context.setResponse(storeClient.getOrder(rawId));
    }

    @When("I delete the order")
    public void deleteCurrentOrder() {
        context.setResponse(storeClient.deleteOrder(context.order().getId()));
    }

    @Then("the response body should match the order details")
    public void verifyResponseBodyMatchesOrder() {
        Order actual = context.response().as(Order.class);
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(context.order());
    }

    @Then("the response message should be the order id")
    public void verifyResponseMessageIsOrderId() {
        ApiResponse body = context.response().as(ApiResponse.class);
        Assertions.assertThat(body.getCode()).isEqualTo(200);
        Assertions.assertThat(body.getMessage()).isEqualTo(String.valueOf(context.order().getId()));
    }

    @Then("the order should no longer exist")
    public void verifyOrderNoLongerExists() {
        Response fetched = storeClient.getOrderUntil(context.order().getId(), r -> r.statusCode() == 404);
        context.setResponse(fetched);
        Assertions.assertThat(fetched.statusCode()).as("GET deleted order").isEqualTo(404);
        Assertions.assertThat(fetched.as(ApiResponse.class).getMessage()).isEqualTo("Order not found");
    }
}

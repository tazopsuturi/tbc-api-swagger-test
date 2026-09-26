package org.example.tbc_swagger_api_project.steps;

import io.cucumber.java.en.Then;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.example.tbc_swagger_api_project.context.TestContext;
import org.example.tbc_swagger_api_project.models.ApiResponse;

import java.util.Map;

/** Generic assertions on the last HTTP response, independent of the resource under test. */
public class ResponseSteps {

    private final TestContext context;

    public ResponseSteps(TestContext context) {
        this.context = context;
    }

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int expectedStatus) {
        Response response = context.response();
        Assertions.assertThat(response.statusCode())
                .as("HTTP status of %s, body: %s", response.getStatusLine(), response.asString())
                .isEqualTo(expectedStatus);
    }

    @Then("the response header {string} should contain {string}")
    public void verifyHeaderContains(String header, String expected) {
        Assertions.assertThat(context.response().header(header))
                .as("Header '%s'", header)
                .isNotNull()
                .containsIgnoringCase(expected);
    }

    @Then("the response headers should include:")
    public void verifyHeadersContain(Map<String, String> expectedHeaders) {
        expectedHeaders.forEach(this::verifyHeaderContains);
    }

    @Then("the response should match the {string} JSON schema")
    public void verifyResponseMatchesJsonSchema(String schemaName) {
        context.response().then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/" + schemaName + ".json"));
    }

    @Then("the response body should be empty")
    public void verifyResponseBodyIsEmpty() {
        Assertions.assertThat(context.response().asString()).isEmpty();
    }

    @Then("the response time should be less than {long} ms")
    public void verifyResponseTimeBelow(long maxMillis) {
        Assertions.assertThat(context.response().time()).as("Response time in ms").isLessThan(maxMillis);
    }

    @Then("the response should be an API error with code {int}, type {string} and message {string}")
    public void verifyApiError(int code, String type, String message) {
        ApiResponse error = context.response().as(ApiResponse.class);
        Assertions.assertThat(error.getCode()).as("error code").isEqualTo(code);
        Assertions.assertThat(error.getType()).as("error type").isEqualTo(type);
        Assertions.assertThat(error.getMessage()).as("error message").isEqualTo(message);
    }

    @Then("the response error message should contain {string}")
    public void verifyErrorMessageContains(String expected) {
        Assertions.assertThat(context.response().as(ApiResponse.class).getMessage()).contains(expected);
    }
}

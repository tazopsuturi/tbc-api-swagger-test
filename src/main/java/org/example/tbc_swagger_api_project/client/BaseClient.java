package org.example.tbc_swagger_api_project.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.tbc_swagger_api_project.config.ApiConfig;

import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class BaseClient {

    private static final RequestSpecification BASE_SPEC = buildBaseSpec();

    protected RequestSpecification request() {
        return RestAssured.given().spec(BASE_SPEC);
    }

    /**
     * The public Petstore runs several backend nodes that are not always in sync, so a freshly
     * written resource may briefly be missing (or a deleted one still present). Re-issue the request
     * until the condition holds or attempts run out, and return the last response either way so the
     * caller's assertions report the real outcome.
     */
    protected Response retryUntil(Supplier<Response> call, Predicate<Response> condition) {
        Response response = call.get();
        for (int attempt = 1; attempt < ApiConfig.pollAttempts() && !condition.test(response); attempt++) {
            sleep(ApiConfig.pollIntervalMillis());
            response = call.get();
        }
        return response;
    }

    private static RequestSpecification buildBaseSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ApiConfig.baseUri())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured());
        if (ApiConfig.loggingEnabled()) {
            builder.addFilter(new RequestLoggingFilter()).addFilter(new ResponseLoggingFilter());
        }
        return builder.build();
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while polling", e);
        }
    }
}

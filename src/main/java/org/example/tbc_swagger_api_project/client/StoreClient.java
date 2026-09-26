package org.example.tbc_swagger_api_project.client;

import io.restassured.response.Response;
import org.example.tbc_swagger_api_project.models.Order;

import java.util.function.Predicate;

public class StoreClient extends BaseClient {

    private static final String ORDER = "/store/order";
    private static final String ORDER_BY_ID = "/store/order/{orderId}";

    public Response placeOrder(Order order) {
        return request().body(order).post(ORDER);
    }

    public Response getOrder(Object orderId) {
        return request().pathParam("orderId", orderId).get(ORDER_BY_ID);
    }

    public Response getOrderUntil(Object orderId, Predicate<Response> condition) {
        return retryUntil(() -> getOrder(orderId), condition);
    }

    public Response deleteOrder(Object orderId) {
        return request().pathParam("orderId", orderId).delete(ORDER_BY_ID);
    }
}

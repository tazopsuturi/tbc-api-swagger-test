package org.example.tbc_swagger_api_project.context;

import io.restassured.response.Response;
import org.example.tbc_swagger_api_project.models.Order;
import org.example.tbc_swagger_api_project.models.Pet;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Per-scenario state shared between step definition classes (injected by cucumber-picocontainer,
 * so every scenario gets a fresh instance). API clients are injected into the steps directly.
 */
public class TestContext {

    private final Set<Long> createdPetIds = new LinkedHashSet<>();
    private final Set<Long> createdOrderIds = new LinkedHashSet<>();

    private Response response;
    /** The pet as the test expects the server to hold it. */
    private Pet pet;
    /** The order as the test expects the server to hold it. */
    private Order order;

    public Response response() {
        if (response == null) {
            throw new IllegalStateException("No request has been sent in this scenario yet");
        }
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Pet pet() {
        if (pet == null) {
            throw new IllegalStateException("No pet has been set up in this scenario");
        }
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Order order() {
        if (order == null) {
            throw new IllegalStateException("No order has been set up in this scenario");
        }
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void trackPet(Long id) {
        createdPetIds.add(id);
    }

    public boolean isTrackedPet(Long id) {
        return createdPetIds.contains(id);
    }

    public Set<Long> createdPetIds() {
        return createdPetIds;
    }

    public void trackOrder(Long id) {
        createdOrderIds.add(id);
    }

    public boolean isTrackedOrder(Long id) {
        return createdOrderIds.contains(id);
    }

    public Set<Long> createdOrderIds() {
        return createdOrderIds;
    }
}

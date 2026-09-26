package org.example.tbc_swagger_api_project.hooks;

import io.cucumber.java.After;
import org.example.tbc_swagger_api_project.client.PetClient;
import org.example.tbc_swagger_api_project.client.StoreClient;
import org.example.tbc_swagger_api_project.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Removes every pet and order a scenario created, so test data does not pile up on the shared server. */
public class CleanupHooks {

    private static final Logger LOG = LoggerFactory.getLogger(CleanupHooks.class);

    private final TestContext context;
    private final PetClient petClient;
    private final StoreClient storeClient;

    public CleanupHooks(TestContext context, PetClient petClient, StoreClient storeClient) {
        this.context = context;
        this.petClient = petClient;
        this.storeClient = storeClient;
    }

    @After
    public void deleteCreatedData() {
        context.createdOrderIds().forEach(id -> deleteQuietly("order", id, () -> storeClient.deleteOrder(id)));
        context.createdPetIds().forEach(id -> deleteQuietly("pet", id, () -> petClient.deletePet(id)));
    }

    private static void deleteQuietly(String resource, Long id, Runnable delete) {
        try {
            delete.run();
        } catch (RuntimeException e) {
            LOG.warn("Cleanup could not delete {} {}: {}", resource, id, e.toString());
        }
    }
}

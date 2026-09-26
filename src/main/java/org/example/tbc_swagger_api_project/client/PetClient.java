package org.example.tbc_swagger_api_project.client;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.tbc_swagger_api_project.config.ApiConfig;
import org.example.tbc_swagger_api_project.models.Pet;

import java.util.function.Predicate;

public class PetClient extends BaseClient {

    private static final String PET = "/pet";
    private static final String PET_BY_ID = "/pet/{petId}";
    private static final String FIND_BY_STATUS = "/pet/findByStatus";

    public Response createPet(Pet pet) {
        return request().body(pet).post(PET);
    }

    public Response createPetRaw(String body, String contentType) {
        return request().contentType(contentType).body(body).post(PET);
    }

    public Response updatePet(Pet pet) {
        return request().body(pet).put(PET);
    }

    public Response updatePetRaw(String body, String contentType) {
        return request().contentType(contentType).body(body).put(PET);
    }

    public Response updatePetWithForm(Object petId, String name, String status) {
        return request()
                .contentType(ContentType.URLENC)
                .pathParam("petId", petId)
                .formParam("name", name)
                .formParam("status", status)
                .post(PET_BY_ID);
    }

    public Response getPet(Object petId) {
        return request().pathParam("petId", petId).get(PET_BY_ID);
    }

    public Response getPetUntil(Object petId, Predicate<Response> condition) {
        return retryUntil(() -> getPet(petId), condition);
    }

    public Response findByStatus(String status) {
        return request().queryParam("status", status).get(FIND_BY_STATUS);
    }

    public Response findByStatusUntil(String status, Predicate<Response> condition) {
        return retryUntil(() -> findByStatus(status), condition);
    }

    public Response deletePet(Object petId) {
        return request().header("api_key", ApiConfig.apiKey()).pathParam("petId", petId).delete(PET_BY_ID);
    }
}

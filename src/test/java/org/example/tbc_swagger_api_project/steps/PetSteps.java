package org.example.tbc_swagger_api_project.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.example.tbc_swagger_api_project.client.PetClient;
import org.example.tbc_swagger_api_project.context.TestContext;
import org.example.tbc_swagger_api_project.data.TestDataFactory;
import org.example.tbc_swagger_api_project.models.ApiResponse;
import org.example.tbc_swagger_api_project.models.Pet;

import java.util.List;

public class PetSteps {

    private final TestContext context;
    private final PetClient petClient;

    public PetSteps(TestContext context, PetClient petClient) {
        this.context = context;
        this.petClient = petClient;
    }

    // ---------- Given ----------

    @Given("a pet named {string} in category {string} with status {string}")
    public void preparePetPayload(String name, String category, String status) {
        context.setPet(TestDataFactory.pet(name, category, status));
    }

    @Given("an existing pet named {string} in category {string} with status {string}")
    public void createExistingPet(String name, String category, String status) {
        preparePetPayload(name, category, status);
        Pet pet = context.pet();

        Response created = petClient.createPet(pet);
        Assertions.assertThat(created.statusCode()).as("Precondition: create pet, body: %s", created.asString()).isEqualTo(200);
        context.trackPet(pet.getId());

        Response fetched = petClient.getPetUntil(pet.getId(), r -> r.statusCode() == 200);
        Assertions.assertThat(fetched.statusCode()).as("Precondition: created pet %d is retrievable", pet.getId()).isEqualTo(200);
    }

    @Given("a pet id that does not exist")
    public void prepareNonExistentPetId() {
        Pet pet = TestDataFactory.pet("Ghost", "None", "available");
        petClient.deletePet(pet.getId());
        Response fetched = petClient.getPetUntil(pet.getId(), r -> r.statusCode() == 404);
        Assertions.assertThat(fetched.statusCode()).as("Precondition: pet %d is absent", pet.getId()).isEqualTo(404);
        context.setPet(pet);
    }

    // ---------- When ----------

    @When("I create the pet")
    public void createPet() {
        context.setResponse(petClient.createPet(context.pet()));
        context.trackPet(context.pet().getId());
    }

    @When("I retrieve the pet")
    public void getCurrentPet() {
        Long id = context.pet().getId();
        // Pets this scenario created are known to exist, so absorb replication lag; otherwise take the first answer.
        context.setResponse(context.isTrackedPet(id)
                ? petClient.getPetUntil(id, r -> r.statusCode() == 200)
                : petClient.getPet(id));
    }

    @When("I retrieve the pet with id {string}")
    public void getPetByRawId(String rawId) {
        context.setResponse(petClient.getPet(rawId));
    }

    @When("I update the pet with name {string} and status {string}")
    public void updateCurrentPetNameAndStatus(String name, String status) {
        Pet pet = context.pet();
        pet.setName(name);
        pet.setStatus(status);
        context.setResponse(petClient.updatePet(pet));
    }

    @When("I update the pet via form data with name {string} and status {string}")
    public void updateCurrentPetNameAndStatusWithForm(String name, String status) {
        Pet pet = context.pet();
        context.setResponse(petClient.updatePetWithForm(pet.getId(), name, status));
        pet.setName(name);
        pet.setStatus(status);
    }

    @When("I delete the pet")
    public void deleteCurrentPet() {
        context.setResponse(petClient.deletePet(context.pet().getId()));
    }

    @When("I delete the pet with id {string}")
    public void deletePetByRawId(String rawId) {
        context.setResponse(petClient.deletePet(rawId));
    }

    @When("I search for pets with status {string}")
    public void findPetsByStatus(String status) {
        context.setResponse(petClient.findByStatus(status));
    }

    @When("I search for pets with status {string} until the pet is listed")
    public void findPetsByStatusUntilCurrentPetListed(String status) {
        Long id = context.pet().getId();
        context.setResponse(petClient.findByStatusUntil(status,
                r -> r.statusCode() == 200 && r.jsonPath().getList("id", Long.class).contains(id)));
    }

    @When("I send a create pet request with body {string} and content type {string}")
    public void createPetWithRawBody(String body, String contentType) {
        context.setResponse(petClient.createPetRaw(body, contentType));
    }

    @When("I send an update pet request with body {string} and content type {string}")
    public void updatePetWithRawBody(String body, String contentType) {
        context.setResponse(petClient.updatePetRaw(body, contentType));
    }

    @Then("the response body should match the pet details")
    public void verifyResponseBodyMatchesPet() {
        Pet actual = context.response().as(Pet.class);
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(context.pet());
    }

    @Then("the response message should be the pet id")
    public void verifyResponseMessageIsPetId() {
        ApiResponse body = context.response().as(ApiResponse.class);
        Assertions.assertThat(body.getCode()).isEqualTo(200);
        Assertions.assertThat(body.getMessage()).isEqualTo(String.valueOf(context.pet().getId()));
    }

    @Then("retrieving the pet should return name {string} and status {string}")
    public void retrievingThePetShouldReturn(String name, String status) {
        Response fetched = petClient.getPetUntil(context.pet().getId(),
                r -> r.statusCode() == 200 && name.equals(r.path("name")) && status.equals(r.path("status")));
        Assertions.assertThat(fetched.statusCode()).isEqualTo(200);
        Pet actual = fetched.as(Pet.class);
        Assertions.assertThat(actual.getName()).isEqualTo(name);
        Assertions.assertThat(actual.getStatus()).isEqualTo(status);
    }

    @Then("the pet should no longer exist")
    public void verifyPetNoLongerExists() {
        Response fetched = petClient.getPetUntil(context.pet().getId(), r -> r.statusCode() == 404);
        context.setResponse(fetched);
        Assertions.assertThat(fetched.statusCode()).as("GET deleted pet").isEqualTo(404);
        Assertions.assertThat(fetched.as(ApiResponse.class).getMessage()).isEqualTo("Pet not found");
    }

    @Then("every pet in the response should have status {string}")
    public void everyPetShouldHaveStatus(String status) {
        List<String> statuses = context.response().jsonPath().getList("status", String.class);
        Assertions.assertThat(statuses).isNotEmpty().allMatch(status::equals);
    }

    @Then("the search results should include the pet")
    public void verifySearchResultsIncludePet() {
        List<Long> ids = context.response().jsonPath().getList("id", Long.class);
        Assertions.assertThat(ids).contains(context.pet().getId());
    }
}

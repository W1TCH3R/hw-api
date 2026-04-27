package api.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import api.client.PetApiClient;
import api.model.Pet;
import api.testdata.TestDataFactory;
import api.util.DeleteHelper;
import api.util.IdGenerator;
import io.restassured.response.Response;

@Tag("api")
public class PetApiTest extends BaseApiTest {

    private static final String AVAILABLE_STATUS = "available";

    private final PetApiClient petApiClient = new PetApiClient();

    @Test
    void shouldCreatePetWithAvailableStatus() {
        long petId = IdGenerator.generateUniqueId();
        Pet pet = TestDataFactory.createAvailablePet(petId, 1);

        try {
            Response response = petApiClient.createPet(pet);
            logStepResponse("Create pet", response);

            assertEquals(200, response.statusCode());
            assertEquals(petId, response.jsonPath().getLong("id"));
            assertEquals("doggie-1", response.jsonPath().getString("name"));
            assertEquals(AVAILABLE_STATUS, response.jsonPath().getString("status"));
        } finally {
            DeleteHelper.safeDelete(
                    () -> petApiClient.deletePet(petId),
                    "pet",
                    petId
            );
        }
    }

    @Test
    void shouldGetCreatedPetById() {
        long petId = IdGenerator.generateUniqueId();
        Pet pet = TestDataFactory.createAvailablePet(petId, 1);

        try {
            Response createResponse = petApiClient.createPet(pet);
            logStepResponse("Create pet for GET test", createResponse);

            assertEquals(200, createResponse.statusCode());

            Response getResponse = petApiClient.getPetById(petId);
            logStepResponse("Get pet by ID", getResponse);

            assertEquals(200, getResponse.statusCode());
            assertEquals(petId, getResponse.jsonPath().getLong("id"));
            assertEquals(AVAILABLE_STATUS, getResponse.jsonPath().getString("status"));
        } finally {
            DeleteHelper.safeDelete(
                    () -> petApiClient.deletePet(petId),
                    "pet",
                    petId
            );
        }
    }

    @Test
    void shouldDeleteCreatedPet() {
        long petId = IdGenerator.generateUniqueId();
        Pet pet = TestDataFactory.createAvailablePet(petId, 1);

        Response createResponse = petApiClient.createPet(pet);
        logStepResponse("Create pet for DELETE test", createResponse);

        assertEquals(200, createResponse.statusCode());

        DeleteHelper.deleteWithRetry(
                () -> petApiClient.deletePet(petId),
                "pet",
                petId
        );

        Response getDeletedPetResponse = petApiClient.getPetById(petId);
        logStepResponse("Get deleted pet by ID", getDeletedPetResponse);

        assertEquals(404, getDeletedPetResponse.statusCode());
    }
}
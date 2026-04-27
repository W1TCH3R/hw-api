package api.tests;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import api.client.PetApiClient;
import api.client.StoreApiClient;
import api.model.Order;
import api.model.Pet;
import api.testdata.TestDataFactory;
import api.util.DeleteHelper;
import api.util.IdGenerator;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@Tag("e2e")
public class PetStoreE2ETest {

    private static final String AVAILABLE_STATUS = "available";
    private static final String ORDER_STATUS = "placed";

    private final PetApiClient petApiClient = new PetApiClient();
    private final StoreApiClient storeApiClient = new StoreApiClient();

    private final List<Long> createdPetIds = new ArrayList<>();
    private final List<Long> createdOrderIds = new ArrayList<>();

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    @Test
    void shouldCreatePetsPlaceOrdersDeleteResourcesAndVerifyTheyCannotBeRetrieved() {
        try {
            createFourAvailablePets();
            verifyCreatedPetsCanBeRetrieved();

            placeMultipleOrdersForEachPet();
            verifyCreatedOrdersCanBeRetrieved();

            deleteCreatedOrders();
            verifyDeletedOrdersCannotBeRetrieved();

            deleteCreatedPets();
            verifyDeletedPetsCannotBeRetrieved();

        } finally {
            cleanupCreatedResources();
        }
    }

    private void createFourAvailablePets() {
        for (int i = 1; i <= 4; i++) {
            long petId = IdGenerator.generateUniqueId();
            Pet pet = TestDataFactory.createAvailablePet(petId, i);

            Response response = petApiClient.createPet(pet);

            assertEquals(200, response.statusCode());
            assertEquals(petId, response.jsonPath().getLong("id"));
            assertEquals("doggie-" + i, response.jsonPath().getString("name"));
            assertEquals(AVAILABLE_STATUS, response.jsonPath().getString("status"));

            createdPetIds.add(petId);
        }
    }

    private void verifyCreatedPetsCanBeRetrieved() {
        for (Long petId : createdPetIds) {
            Response response = petApiClient.getPetById(petId);

            assertEquals(200, response.statusCode());
            assertEquals(petId, response.jsonPath().getLong("id"));
            assertEquals(AVAILABLE_STATUS, response.jsonPath().getString("status"));
        }
    }

    private void placeMultipleOrdersForEachPet() {
        int ordersPerPet = 2;

        for (Long petId : createdPetIds) {
            for (int i = 1; i <= ordersPerPet; i++) {
                long orderId = IdGenerator.generateUniqueId();
                Order order = TestDataFactory.createOrder(orderId, petId);

                Response response = storeApiClient.createOrder(order);

                assertEquals(200, response.statusCode());
                assertEquals(orderId, response.jsonPath().getLong("id"));
                assertEquals(petId, response.jsonPath().getLong("petId"));
                assertEquals(ORDER_STATUS, response.jsonPath().getString("status"));
                assertEquals(true, response.jsonPath().getBoolean("complete"));

                createdOrderIds.add(orderId);
            }
        }
    }

    private void verifyCreatedOrdersCanBeRetrieved() {
        for (Long orderId : createdOrderIds) {
            Response response = storeApiClient.getOrderById(orderId);

            assertEquals(200, response.statusCode());
            assertEquals(orderId, response.jsonPath().getLong("id"));
            assertEquals(ORDER_STATUS, response.jsonPath().getString("status"));
        }
    }

    private void deleteCreatedOrders() {
        for (Long orderId : createdOrderIds) {
            DeleteHelper.deleteWithRetry(
                    () -> storeApiClient.deleteOrder(orderId),
                    "order",
                    orderId
            );
        }
    }

    private void verifyDeletedOrdersCannotBeRetrieved() {
        for (Long orderId : createdOrderIds) {
            Response response = storeApiClient.getOrderById(orderId);

            assertEquals(404, response.statusCode());
        }
    }

    private void deleteCreatedPets() {
        for (Long petId : createdPetIds) {
            DeleteHelper.deleteWithRetry(
                    () -> petApiClient.deletePet(petId),
                    "pet",
                    petId
            );
        }
    }

    private void verifyDeletedPetsCannotBeRetrieved() {
        for (Long petId : createdPetIds) {
            Response response = petApiClient.getPetById(petId);

            assertEquals(404, response.statusCode());
        }
    }

    private void cleanupCreatedResources() {
        for (Long orderId : createdOrderIds) {
            DeleteHelper.safeDelete(
                    () -> storeApiClient.deleteOrder(orderId),
                    "order",
                    orderId
            );
        }

        for (Long petId : createdPetIds) {
            DeleteHelper.safeDelete(
                    () -> petApiClient.deletePet(petId),
                    "pet",
                    petId
            );
        }
    }
}
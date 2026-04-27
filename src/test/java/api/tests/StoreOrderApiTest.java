package api.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import api.client.PetApiClient;
import api.client.StoreApiClient;
import api.model.Order;
import api.model.Pet;
import api.testdata.TestDataFactory;
import api.util.DeleteHelper;
import api.util.IdGenerator;
import io.restassured.response.Response;

@Tag("api")
public class StoreOrderApiTest extends BaseApiTest {

    private static final String ORDER_STATUS = "placed";

    private final PetApiClient petApiClient = new PetApiClient();
    private final StoreApiClient storeApiClient = new StoreApiClient();

    @Test
    void shouldCreateOrderForExistingPet() {
        long petId = IdGenerator.generateUniqueId();
        long orderId = IdGenerator.generateUniqueId();

        Pet pet = TestDataFactory.createAvailablePet(petId, 1);
        Order order = TestDataFactory.createOrder(orderId, petId);

        try {
            Response createPetResponse = petApiClient.createPet(pet);
            logStepResponse("Create pet for order test", createPetResponse);

            assertEquals(200, createPetResponse.statusCode());

            Response createOrderResponse = storeApiClient.createOrder(order);
            logStepResponse("Create order", createOrderResponse);

            assertEquals(200, createOrderResponse.statusCode());
            assertEquals(orderId, createOrderResponse.jsonPath().getLong("id"));
            assertEquals(petId, createOrderResponse.jsonPath().getLong("petId"));
            assertEquals(ORDER_STATUS, createOrderResponse.jsonPath().getString("status"));
            assertTrue(createOrderResponse.jsonPath().getBoolean("complete"));
        } finally {
            DeleteHelper.safeDelete(
                    () -> storeApiClient.deleteOrder(orderId),
                    "order",
                    orderId
            );

            DeleteHelper.safeDelete(
                    () -> petApiClient.deletePet(petId),
                    "pet",
                    petId
            );
        }
    }

    @Test
    void shouldGetCreatedOrderById() {
        long petId = IdGenerator.generateUniqueId();
        long orderId = IdGenerator.generateUniqueId();

        Pet pet = TestDataFactory.createAvailablePet(petId, 1);
        Order order = TestDataFactory.createOrder(orderId, petId);

        try {
            Response createPetResponse = petApiClient.createPet(pet);
            logStepResponse("Create pet for GET order test", createPetResponse);

            assertEquals(200, createPetResponse.statusCode());

            Response createOrderResponse = storeApiClient.createOrder(order);
            logStepResponse("Create order for GET test", createOrderResponse);

            assertEquals(200, createOrderResponse.statusCode());

            Response getOrderResponse = storeApiClient.getOrderById(orderId);
            logStepResponse("Get order by ID", getOrderResponse);

            assertEquals(200, getOrderResponse.statusCode());
            assertEquals(orderId, getOrderResponse.jsonPath().getLong("id"));
            assertEquals(petId, getOrderResponse.jsonPath().getLong("petId"));
            assertEquals(ORDER_STATUS, getOrderResponse.jsonPath().getString("status"));
        } finally {
            DeleteHelper.safeDelete(
                    () -> storeApiClient.deleteOrder(orderId),
                    "order",
                    orderId
            );

            DeleteHelper.safeDelete(
                    () -> petApiClient.deletePet(petId),
                    "pet",
                    petId
            );
        }
    }

    @Test
    void shouldDeleteCreatedOrder() {
        long petId = IdGenerator.generateUniqueId();
        long orderId = IdGenerator.generateUniqueId();

        Pet pet = TestDataFactory.createAvailablePet(petId, 1);
        Order order = TestDataFactory.createOrder(orderId, petId);

        try {
            Response createPetResponse = petApiClient.createPet(pet);
            logStepResponse("Create pet for DELETE order test", createPetResponse);

            assertEquals(200, createPetResponse.statusCode());

            Response createOrderResponse = storeApiClient.createOrder(order);
            logStepResponse("Create order for DELETE test", createOrderResponse);

            assertEquals(200, createOrderResponse.statusCode());

            DeleteHelper.deleteWithRetry(
                    () -> storeApiClient.deleteOrder(orderId),
                    "order",
                    orderId
            );

            Response getDeletedOrderResponse = storeApiClient.getOrderById(orderId);
            logStepResponse("Get deleted order by ID", getDeletedOrderResponse);

            assertEquals(404, getDeletedOrderResponse.statusCode());
        } finally {
            DeleteHelper.safeDelete(
                    () -> storeApiClient.deleteOrder(orderId),
                    "order",
                    orderId
            );

            DeleteHelper.safeDelete(
                    () -> petApiClient.deletePet(petId),
                    "pet",
                    petId
            );
        }
    }
}
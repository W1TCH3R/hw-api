package api.testdata;

import java.time.Instant;
import java.util.List;

import api.model.Category;
import api.model.Order;
import api.model.Pet;
import api.model.Tag;

public class TestDataFactory {

    private TestDataFactory() {
    }

    public static Pet createAvailablePet(long petId, int index) {
        return new Pet(
                petId,
                new Category(index, "dogs"),
                "doggie-" + index,
                List.of("https://example.com/photo-" + index + ".jpg"),
                List.of(new Tag(index, "automation-test")),
                "available"
        );
    }

    public static Order createOrder(long orderId, long petId) {
        return new Order(
                orderId,
                petId,
                1,
                Instant.now().toString(),
                "placed",
                true
        );
    }
}
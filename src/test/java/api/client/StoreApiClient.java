package api.client;

import api.model.Order;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class StoreApiClient {

    private static final String API_KEY = "special-key";

    public Response createOrder(Order order) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(order)
                .log().all()
        .when()
                .post("/store/order")
        .then()
                .log().all()
                .extract()
                .response();
    }

    public Response getOrderById(long orderId) {
        return given()
                .accept(ContentType.JSON)
                .log().all()
        .when()
                .get("/store/order/{orderId}", orderId)
        .then()
                .log().all()
                .extract()
                .response();
    }

    public Response deleteOrder(long orderId) {
        return given()
                .header("api_key", API_KEY)
                .accept(ContentType.JSON)
                .log().all()
        .when()
                .delete("/store/order/{orderId}", orderId)
        .then()
                .log().all()
                .extract()
                .response();
    }
}
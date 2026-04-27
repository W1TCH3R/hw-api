package api.client;

import api.model.Pet;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class PetApiClient {

    private static final String API_KEY = "special-key";

    public Response createPet(Pet pet) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(pet)
                .log().all()
        .when()
                .post("/pet")
        .then()
                .log().all()
                .extract()
                .response();
    }

    public Response getPetById(long petId) {
        return given()
                .accept(ContentType.JSON)
                .log().all()
        .when()
                .get("/pet/{petId}", petId)
        .then()
                .log().all()
                .extract()
                .response();
    }

    public Response deletePet(long petId) {
        return given()
                .header("api_key", API_KEY)
                .accept(ContentType.JSON)
                .log().all()
        .when()
                .delete("/pet/{petId}", petId)
        .then()
                .log().all()
                .extract()
                .response();
    }
}
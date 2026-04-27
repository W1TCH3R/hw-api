package api.tests;

import org.junit.jupiter.api.BeforeAll;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class BaseApiTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    protected void logStepResponse(String stepName, Response response) {
        System.out.println();
        System.out.println("========== " + stepName + " ==========");
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response body:");
        System.out.println(response.asPrettyString());
        System.out.println("======================================");
        System.out.println();
    }
}
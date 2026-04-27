package api.util;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.fail;

import io.restassured.response.Response;

public class DeleteHelper {

    private static final int MAX_ATTEMPTS = 3;

    private DeleteHelper() {
    }

    public static void deleteWithRetry(
            Supplier<Response> deleteAction,
            String resourceType,
            long resourceId
    ) {
        Response lastResponse = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            lastResponse = deleteAction.get();
            int statusCode = lastResponse.statusCode();

            if (isSuccessfulDeleteStatus(statusCode)) {
                return;
            }

            System.out.printf(
                    "Failed to delete %s with id %s. Attempt %d/%d. Status: %d. Body: %s%n",
                    resourceType,
                    resourceId,
                    attempt,
                    MAX_ATTEMPTS,
                    statusCode,
                    lastResponse.asString()
            );

            waitBeforeRetry();
        }

        fail(String.format(
                "Could not delete %s with id %s after %d attempts. Last status: %d. Body: %s",
                resourceType,
                resourceId,
                MAX_ATTEMPTS,
                lastResponse != null ? lastResponse.statusCode() : -1,
                lastResponse != null ? lastResponse.asString() : "No response"
        ));
    }

    public static void safeDelete(
            Supplier<Response> deleteAction,
            String resourceType,
            long resourceId
    ) {
        try {
            Response response = deleteAction.get();
            int statusCode = response.statusCode();

            if (!isSuccessfulDeleteStatus(statusCode)) {
                System.out.printf(
                        "Cleanup failed for %s with id %s. Status: %d. Body: %s%n",
                        resourceType,
                        resourceId,
                        statusCode,
                        response.asString()
                );
            }
        } catch (Exception e) {
            System.out.printf(
                    "Cleanup exception for %s with id %s: %s%n",
                    resourceType,
                    resourceId,
                    e.getMessage()
            );
        }
    }

    private static boolean isSuccessfulDeleteStatus(int statusCode) {
        return statusCode == 200
                || statusCode == 202
                || statusCode == 204
                || statusCode == 404;
    }

    private static void waitBeforeRetry() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Retry wait was interrupted");
        }
    }
}
package api.util;

import java.util.concurrent.ThreadLocalRandom;

public class IdGenerator {

    private IdGenerator() {
    }

    public static long generateUniqueId() {
        return ThreadLocalRandom.current().nextLong(100_000, 999_999_999);
    }
}
package de.ostfalia.ebike2020;

import java.util.Random;

public class BusinessKeyGenerator {
    private static final String LETTER = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    public static String getKey(int size) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append(LETTER.charAt(RANDOM.nextInt(LETTER.length())));
        }
        return builder.toString();
    }
}

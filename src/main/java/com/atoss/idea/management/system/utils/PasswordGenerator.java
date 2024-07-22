package com.atoss.idea.management.system.utils;

import java.security.SecureRandom;

public class PasswordGenerator {

    /**
     * Generates a random password of the specified length using characters from a predefined set.
     *
     * @param len The length of the password to be generated.
     *
     * @return A randomly generated password as a string.
     *
     * @see SecureRandom
     */
    public static String generatePassayPassword(int len) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return generatePassword(len, chars);
    }

    /**
     * Generates a random sequence of numbers of the specified length.
     *
     * @param len The length of the code to be generated.
     *
     * @return A randomly generated code as a string.
     *
     * @see SecureRandom
     */
    public static String generateOTP(int len) {
        final String chars = "0123456789";
        return generatePassword(len, chars);
    }

    private static String generatePassword(int len, String chars) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }
}

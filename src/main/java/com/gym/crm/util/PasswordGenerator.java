package com.gym.crm.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordGenerator {

    private static final int PASSWORD_LENGTH = 10;

    private static final String ALLOWED_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "abcdefghijklmnopqrstuvwxyz"
                    + "0123456789";

    private final SecureRandom secureRandom = new SecureRandom();

    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int index = 0; index < PASSWORD_LENGTH; index++) {
            int randomIndex = secureRandom.nextInt(ALLOWED_CHARACTERS.length());

            password.append(ALLOWED_CHARACTERS.charAt(randomIndex));
        }

        return password.toString();
    }
}
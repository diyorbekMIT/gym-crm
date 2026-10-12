package com.gym.crm.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordGeneratorTest {

    private final PasswordGenerator passwordGenerator = new PasswordGenerator();

    @Test
    void shouldGeneratePasswordWithTenCharacters() {
        String password = passwordGenerator.generatePassword();

        assertThat(password).hasSize(10);
    }

    @Test
    void shouldGeneratePasswordUsingOnlyAllowedCharacters() {
        String password = passwordGenerator.generatePassword();

        assertThat(password).matches("[A-Za-z0-9]{10}");
    }

    @Test
    void shouldUsuallyGenerateDifferentPasswords() {
        String firstPassword = passwordGenerator.generatePassword();
        String secondPassword = passwordGenerator.generatePassword();

        assertThat(firstPassword).isNotEqualTo(secondPassword);
    }
}
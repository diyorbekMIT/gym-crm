package com.gym.crm.util;

import com.gym.crm.dao.jpa.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JpaUsernameGeneratorTest {

    private UserDao userDao;
    private JpaUsernameGenerator usernameGenerator;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);

        usernameGenerator = new JpaUsernameGenerator(userDao);
    }

    @Test
    void shouldUseBaseUsernameWhenAvailable() {
        String username =
                usernameGenerator.generateUsername("John", "Smith");

        assertThat(username).isEqualTo("John.Smith");
    }

    @Test
    void shouldAddSuffixWhenBaseUsernameExists() {
        when(userDao.existsByUsername("John.Smith"))
                .thenReturn(true);

        String username =
                usernameGenerator.generateUsername("John", "Smith");

        assertThat(username).isEqualTo("John.Smith1");
    }

    @Test
    void shouldSkipOccupiedSuffixes() {
        when(userDao.existsByUsername("John.Smith"))
                .thenReturn(true);

        when(userDao.existsByUsername("John.Smith1"))
                .thenReturn(true);

        when(userDao.existsByUsername("John.Smith2"))
                .thenReturn(true);

        String username =
                usernameGenerator.generateUsername("John", "Smith");

        assertThat(username).isEqualTo("John.Smith3");
    }

    @Test
    void shouldTrimNames() {
        String username =
                usernameGenerator.generateUsername(" John ", " Smith ");

        assertThat(username).isEqualTo("John.Smith");
    }

    @Test
    void shouldRejectNullFirstNameBeforeQueryingDatabase() {
        assertThatThrownBy(
                () -> usernameGenerator.generateUsername(null, "Smith")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name must not be blank.");

        verifyNoInteractions(userDao);
    }

    @Test
    void shouldRejectBlankLastNameBeforeQueryingDatabase() {
        assertThatThrownBy(
                () -> usernameGenerator.generateUsername("John", " ")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Last name must not be blank.");

        verifyNoInteractions(userDao);
    }
}
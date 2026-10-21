package com.gym.crm.util;

import com.gym.crm.dao.jpa.UserDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("jpaUsernameGenerator")
@Qualifier("jpaUsernameGenerator")
public class JpaUsernameGenerator implements UsernameGenerator {

    private final UserDao userDao;

    public JpaUsernameGenerator(UserDao userDao) {
        this.userDao = userDao;
    }

    public String generateUsername(String firstName, String lastName) {
        validateName(firstName, "First name");
        validateName(lastName, "Last name");

        String baseUsername =
                firstName.trim() + "." + lastName.trim();

        String candidate = baseUsername;
        int suffix = 1;

        while (userDao.existsByUsername(candidate)) {
            candidate = baseUsername + suffix;
            suffix++;
        }

        return candidate;
    }

    private void validateName(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank."
            );
        }
    }
}
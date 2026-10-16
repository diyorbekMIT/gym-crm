package com.gym.crm.util;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class UsernameGenerator {

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    @Autowired
    public UsernameGenerator(TraineeDao traineeDao, TrainerDao trainerDao) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    public String generateUsername(String firstName, String lastName) {
        validateName(firstName, "First name");
        validateName(lastName, "Last name");

        String baseUsername = firstName.trim() + "." + lastName.trim();

        if (!usernameExists(baseUsername)) {
            return baseUsername;
        }

        int suffix = 1;

        while (usernameExists(baseUsername + suffix)) {
            suffix++;
        }

        return baseUsername + suffix;
    }

    private boolean usernameExists(String username) {
        return traineeDao.findByUsername(username).isPresent()
                || trainerDao.findByUsername(username).isPresent();
    }

    private void validateName(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
    }
}
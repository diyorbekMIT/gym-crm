package com.gym.crm.service.jpa;

import com.gym.crm.dao.jpa.TraineeDao;
import com.gym.crm.dao.jpa.TrainerDao;
import com.gym.crm.dao.jpa.TrainingTypeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import com.gym.crm.service.exception.EntityNotFoundException;
import com.gym.crm.util.UsernameGenerator;
import com.gym.crm.util.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ProfileRegistrationService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProfileRegistrationService.class);

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public ProfileRegistrationService(
            TraineeDao traineeDao,
            TrainerDao trainerDao,
            TrainingTypeDao trainingTypeDao,
            @Qualifier("jpaUsernameGenerator") UsernameGenerator usernameGenerator,
            PasswordGenerator passwordGenerator
    ) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.trainingTypeDao = trainingTypeDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    @Transactional
    public Trainee createTrainee(
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String address
    ) {
        User user = newUser(firstName, lastName);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        Trainee savedTrainee = traineeDao.save(trainee);

        LOGGER.info(
                "Registered trainee: id={}, username='{}'.",
                savedTrainee.getId(),
                user.getUsername()
        );

        return savedTrainee;
    }

    @Transactional
    public Trainer createTrainer(
            String firstName,
            String lastName,
            Long specializationId
    ) {
        if (specializationId == null || specializationId <= 0) {
            throw new IllegalArgumentException(
                    "Specialization ID must be a positive number."
            );
        }

        TrainingType specialization = trainingTypeDao
                .findById(specializationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Training type with id="
                                + specializationId
                                + " was not found."
                ));

        User user = newUser(firstName, lastName);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);

        Trainer savedTrainer = trainerDao.save(trainer);

        LOGGER.info(
                "Registered trainer: id={}, username='{}'.",
                savedTrainer.getId(),
                user.getUsername()
        );

        return savedTrainer;
    }

    private User newUser(String firstName, String lastName) {
        String username = usernameGenerator.generateUsername(
                firstName,
                lastName
        );

        User user = new User();
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setUsername(username);
        user.setPassword(passwordGenerator.generatePassword());
        user.setActive(true);

        return user;
    }
}
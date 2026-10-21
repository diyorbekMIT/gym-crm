package com.gym.crm.service.jpa;

import com.gym.crm.dao.jpa.TrainingTypeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import com.gym.crm.service.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class ProfileManagementService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProfileManagementService.class);

    private final AuthenticationService authenticationService;
    private final TrainingTypeDao trainingTypeDao;

    public ProfileManagementService(
            AuthenticationService authenticationService,
            TrainingTypeDao trainingTypeDao
    ) {
        this.authenticationService = authenticationService;
        this.trainingTypeDao = trainingTypeDao;
    }

    public Trainee updateTrainee(
            String username,
            String password,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String address
    ) {
        Trainee trainee = authenticationService.authenticateTrainee(
                username,
                password
        );

        validateNames(firstName, lastName);

        trainee.getUser().setFirstName(firstName.trim());
        trainee.getUser().setLastName(lastName.trim());
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        LOGGER.info("Updated trainee id={}.", trainee.getId());

        return trainee;
    }

    public Trainer updateTrainer(
            String username,
            String password,
            String firstName,
            String lastName,
            Long specializationId
    ) {
        Trainer trainer = authenticationService.authenticateTrainer(
                username,
                password
        );

        validateNames(firstName, lastName);

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

        trainer.getUser().setFirstName(firstName.trim());
        trainer.getUser().setLastName(lastName.trim());
        trainer.setSpecialization(specialization);

        LOGGER.info("Updated trainer id={}.", trainer.getId());

        return trainer;
    }

    public void activateTrainee(String username, String password) {
        Trainee trainee = authenticationService.authenticateTrainee(
                username,
                password
        );

        changeActiveStatus(trainee.getUser(), true);

        LOGGER.info("Activated trainee id={}.", trainee.getId());
    }

    public void deactivateTrainee(String username, String password) {
        Trainee trainee = authenticationService.authenticateTrainee(
                username,
                password
        );

        changeActiveStatus(trainee.getUser(), false);

        LOGGER.info("Deactivated trainee id={}.", trainee.getId());
    }

    public void activateTrainer(String username, String password) {
        Trainer trainer = authenticationService.authenticateTrainer(
                username,
                password
        );

        changeActiveStatus(trainer.getUser(), true);

        LOGGER.info("Activated trainer id={}.", trainer.getId());
    }

    public void deactivateTrainer(String username, String password) {
        Trainer trainer = authenticationService.authenticateTrainer(
                username,
                password
        );

        changeActiveStatus(trainer.getUser(), false);

        LOGGER.info("Deactivated trainer id={}.", trainer.getId());
    }

    private void validateNames(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException(
                    "First name must not be blank."
            );
        }

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException(
                    "Last name must not be blank."
            );
        }
    }

    private void changeActiveStatus(User user, boolean requestedStatus) {
        if (Boolean.valueOf(requestedStatus).equals(user.getActive())) {
            throw new IllegalStateException(
                    requestedStatus
                            ? "Profile is already active."
                            : "Profile is already inactive."
            );
        }

        user.setActive(requestedStatus);
    }
}
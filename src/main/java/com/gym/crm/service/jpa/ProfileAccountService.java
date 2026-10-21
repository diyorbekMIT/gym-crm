package com.gym.crm.service.jpa;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileAccountService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProfileAccountService.class);

    private final AuthenticationService authenticationService;

    public ProfileAccountService(
            AuthenticationService authenticationService
    ) {
        this.authenticationService = authenticationService;
    }

    @Transactional(readOnly = true)
    public Trainee getTraineeProfile(
            String username,
            String password
    ) {
        return authenticationService.authenticateTrainee(
                username,
                password
        );
    }

    @Transactional(readOnly = true)
    public Trainer getTrainerProfile(
            String username,
            String password
    ) {
        return authenticationService.authenticateTrainer(
                username,
                password
        );
    }

    @Transactional
    public void changeTraineePassword(
            String username,
            String currentPassword,
            String newPassword
    ) {
        Trainee trainee = authenticationService.authenticateTrainee(
                username,
                currentPassword
        );

        validateNewPassword(newPassword);

        trainee.getUser().setPassword(newPassword);

        LOGGER.info(
                "Changed password for trainee id={}.",
                trainee.getId()
        );
    }

    @Transactional
    public void changeTrainerPassword(
            String username,
            String currentPassword,
            String newPassword
    ) {
        Trainer trainer = authenticationService.authenticateTrainer(
                username,
                currentPassword
        );

        validateNewPassword(newPassword);

        trainer.getUser().setPassword(newPassword);

        LOGGER.info(
                "Changed password for trainer id={}.",
                trainer.getId()
        );
    }

    private void validateNewPassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "New password must not be blank."
            );
        }
    }
}
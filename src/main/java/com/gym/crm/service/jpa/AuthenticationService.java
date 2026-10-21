package com.gym.crm.service.jpa;

import com.gym.crm.dao.jpa.TraineeDao;
import com.gym.crm.dao.jpa.TrainerDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.User;
import com.gym.crm.service.exception.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthenticationService {

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    public AuthenticationService(
            TraineeDao traineeDao,
            TrainerDao trainerDao
    ) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    public Trainee authenticateTrainee(
            String username,
            String password
    ) {
        validateCredentials(username, password);

        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(AuthenticationException::new);

        verifyPassword(trainee.getUser(), password);

        return trainee;
    }

    public Trainer authenticateTrainer(
            String username,
            String password
    ) {
        validateCredentials(username, password);

        Trainer trainer = trainerDao.findByUsername(username)
                .orElseThrow(AuthenticationException::new);

        verifyPassword(trainer.getUser(), password);

        return trainer;
    }

    private void validateCredentials(
            String username,
            String password
    ) {
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new AuthenticationException();
        }
    }

    private void verifyPassword(User user, String password) {
        if (!password.equals(user.getPassword())) {
            throw new AuthenticationException();
        }
    }
}
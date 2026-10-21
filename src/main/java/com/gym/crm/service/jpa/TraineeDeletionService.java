package com.gym.crm.service.jpa;

import com.gym.crm.dao.jpa.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class TraineeDeletionService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TraineeDeletionService.class);

    private final AuthenticationService authenticationService;
    private final TraineeDao traineeDao;

    public TraineeDeletionService(
            AuthenticationService authenticationService,
            TraineeDao traineeDao
    ) {
        this.authenticationService = authenticationService;
        this.traineeDao = traineeDao;
    }

    @Transactional
    public void deleteTrainee(
            String username,
            String password
    ) {
        Trainee trainee = authenticationService.authenticateTrainee(
                username,
                password
        );

        Long traineeId = trainee.getId();

        for (Trainer trainer :
                new ArrayList<>(trainee.getTrainers())) {

            trainee.removeTrainer(trainer);
        }

        traineeDao.delete(trainee);

        LOGGER.info(
                "Requested hard deletion of trainee id={}.",
                traineeId
        );
    }
}
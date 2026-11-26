package com.gym.crm.service.jpa;

import com.gym.crm.client.TrainerWorkloadClient;
import com.gym.crm.dao.jpa.TraineeDao;
import com.gym.crm.dto.request.TrainerWorkloadRequest;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TraineeDeletionService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TraineeDeletionService.class);

    private final AuthenticationService authenticationService;
    private final TraineeDao traineeDao;
    private final TrainerWorkloadClient  trainerWorkloadClient;

    public TraineeDeletionService(
            AuthenticationService authenticationService,
            TraineeDao traineeDao,
            TrainerWorkloadClient trainerWorkloadClient
    ) {
        this.authenticationService = authenticationService;
        this.traineeDao = traineeDao;
        this.trainerWorkloadClient = trainerWorkloadClient;
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
        List<Training> trainingsToDelete = new ArrayList<>(trainee.getTrainings());

        for (Trainer trainer :
                new ArrayList<>(trainee.getTrainers())) {

            trainee.removeTrainer(trainer);
        }

        traineeDao.delete(trainee);

        for (Training training : trainingsToDelete) {
            trainerWorkloadClient.notifyWorkload(
                    training,
                    TrainerWorkloadRequest.ActionType.DELETE
            );
        }

        LOGGER.info(
                "Requested hard deletion of trainee id={}.",
                traineeId
        );
    }
}
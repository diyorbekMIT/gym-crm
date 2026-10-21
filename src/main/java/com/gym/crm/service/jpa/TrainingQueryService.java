package com.gym.crm.service.jpa;

import com.gym.crm.dao.jpa.TrainingDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TrainingQueryService {

    private final AuthenticationService authenticationService;
    private final TrainingDao trainingDao;

    public TrainingQueryService(
            AuthenticationService authenticationService,
            TrainingDao trainingDao
    ) {
        this.authenticationService = authenticationService;
        this.trainingDao = trainingDao;
    }

    public List<Training> getTraineeTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            Long trainingTypeId
    ) {
        Trainee trainee = authenticationService.authenticateTrainee(
                username,
                password
        );

        validateDateRange(fromDate, toDate);

        if (trainingTypeId != null && trainingTypeId <= 0) {
            throw new IllegalArgumentException(
                    "Training type ID must be a positive number."
            );
        }

        return trainingDao.findForTrainee(
                trainee.getId(),
                fromDate,
                toDate,
                trainerName,
                trainingTypeId
        );
    }

    public List<Training> getTrainerTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        Trainer trainer = authenticationService.authenticateTrainer(
                username,
                password
        );

        validateDateRange(fromDate, toDate);

        return trainingDao.findForTrainer(
                trainer.getId(),
                fromDate,
                toDate,
                traineeName
        );
    }

    private void validateDateRange(
            LocalDate fromDate,
            LocalDate toDate
    ) {
        if (fromDate != null
                && toDate != null
                && fromDate.isAfter(toDate)) {

            throw new IllegalArgumentException(
                    "From date must not be after to date."
            );
        }
    }
}
package com.gym.crm.dao;

import com.gym.crm.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingDao {

    Training save(Training training);

    Optional<Training> findById(Long id);

    List<Training> findAll();

    List<Training> findForTrainee(
            Long traineeId,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            Long trainingTypeId
    );

    List<Training> findForTrainer(
            Long trainerId,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    );
}
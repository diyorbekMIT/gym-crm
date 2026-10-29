package com.gym.crm.mapper;

import com.gym.crm.dto.response.TrainingResponse;
import com.gym.crm.dto.response.TrainingTypeResponse;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainingMapper {

    public TrainingResponse toTraineeTrainingResponse(Training training) {
        return new TrainingResponse(
                training.getTrainingName(),
                training.getTrainingDate(),
                training.getTrainingType().getTrainingTypeName(),
                training.getTrainingDuration(),
                trainerName(training)
        );
    }

    public TrainingResponse toTrainerTrainingResponse(Training training) {
        return new TrainingResponse(
                training.getTrainingName(),
                training.getTrainingDate(),
                training.getTrainingType().getTrainingTypeName(),
                training.getTrainingDuration(),
                traineeName(training)
        );
    }

    public TrainingTypeResponse toTrainingTypeResponse(
            TrainingType trainingType
    ) {
        return new TrainingTypeResponse(
                trainingType.getId(),
                trainingType.getTrainingTypeName()
        );
    }

    public List<TrainingResponse> toTraineeTrainingResponses(
            List<Training> trainings
    ) {
        if (trainings == null) {
            return List.of();
        }

        return trainings.stream()
                .map(this::toTraineeTrainingResponse)
                .toList();
    }

    public List<TrainingResponse> toTrainerTrainingResponses(
            List<Training> trainings
    ) {
        if (trainings == null) {
            return List.of();
        }

        return trainings.stream()
                .map(this::toTrainerTrainingResponse)
                .toList();
    }

    private String trainerName(Training training) {
        return training.getTrainer().getUser().getFirstName()
                + " "
                + training.getTrainer().getUser().getLastName();
    }

    private String traineeName(Training training) {
        return training.getTrainee().getUser().getFirstName()
                + " "
                + training.getTrainee().getUser().getLastName();
    }
}
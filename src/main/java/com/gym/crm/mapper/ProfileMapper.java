package com.gym.crm.mapper;

import com.gym.crm.dto.response.TraineeProfileResponse;
import com.gym.crm.dto.response.TraineeSummaryResponse;
import com.gym.crm.dto.response.TrainerProfileResponse;
import com.gym.crm.dto.response.TrainerSummaryResponse;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class ProfileMapper {

    public TraineeProfileResponse toTraineeProfileResponse(
            Trainee trainee
    ) {
        return new TraineeProfileResponse(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                Boolean.TRUE.equals(trainee.getUser().getActive()),
                toTrainerSummaries(trainee.getTrainers())
        );
    }

    public TrainerProfileResponse toTrainerProfileResponse(
            Trainer trainer
    ) {
        return new TrainerProfileResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization().getId(),
                trainer.getSpecialization().getTrainingTypeName(),
                Boolean.TRUE.equals(trainer.getUser().getActive()),
                toTraineeSummaries(trainer.getTrainees())
        );
    }

    public TrainerSummaryResponse toTrainerSummary(Trainer trainer) {
        return new TrainerSummaryResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization().getId(),
                trainer.getSpecialization().getTrainingTypeName()
        );
    }

    public TraineeSummaryResponse toTraineeSummary(Trainee trainee) {
        return new TraineeSummaryResponse(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName()
        );
    }

    public List<TrainerSummaryResponse> toTrainerSummaries(
            Collection<Trainer> trainers
    ) {
        if (trainers == null) {
            return List.of();
        }

        return trainers.stream()
                .map(this::toTrainerSummary)
                .toList();
    }

    public List<TraineeSummaryResponse> toTraineeSummaries(
            Collection<Trainee> trainees
    ) {
        if (trainees == null) {
            return List.of();
        }

        return trainees.stream()
                .map(this::toTraineeSummary)
                .toList();
    }
}
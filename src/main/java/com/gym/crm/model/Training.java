package com.gym.crm.model;

import java.time.LocalDate;
import java.util.Objects;

public class Training {

    private Long id;
    private Long traineeId;
    private Long trainerId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private Integer trainingDuration;

    public Training() {
    }

    public Training(
            Long id,
            Long traineeId,
            Long trainerId,
            String trainingName,
            TrainingType trainingType,
            LocalDate trainingDate,
            Integer trainingDuration
    ) {
        this.id = id;
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(Long traineeId) {
        this.traineeId = traineeId;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public Integer getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Integer trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Training training = (Training) o;

        return Objects.equals(id, training.id)
                && Objects.equals(traineeId, training.traineeId)
                && Objects.equals(trainerId, training.trainerId)
                && Objects.equals(trainingName, training.trainingName)
                && Objects.equals(trainingType, training.trainingType)
                && Objects.equals(trainingDate, training.trainingDate)
                && Objects.equals(trainingDuration, training.trainingDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                traineeId,
                trainerId,
                trainingName,
                trainingType,
                trainingDate,
                trainingDuration
        );
    }

    @Override
    public String toString() {
        return "Training{"
                + "id=" + id
                + ", traineeId=" + traineeId
                + ", trainerId=" + trainerId
                + ", trainingName='" + trainingName + '\''
                + ", trainingType=" + trainingType
                + ", trainingDate=" + trainingDate
                + ", trainingDuration=" + trainingDuration
                + '}';
    }
}
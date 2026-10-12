package com.gym.crm.model;

import java.util.Objects;

public class Trainer extends User {

    private Long userId;
    private TrainingType specialization;

    public Trainer() {
    }

    public Trainer(
            Long userId,
            String firstName,
            String lastName,
            String username,
            String password,
            boolean active,
            TrainingType specialization
    ) {
        super(firstName, lastName, username, password, active);
        this.userId = userId;
        this.specialization = specialization;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TrainingType getSpecialization() {
        return specialization;
    }

    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        Trainer trainer = (Trainer) o;

        return Objects.equals(userId, trainer.userId)
                && Objects.equals(specialization, trainer.specialization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, specialization);
    }

    @Override
    public String toString() {
        return "Trainer{"
                + "userId=" + userId
                + ", firstName='" + getFirstName() + '\''
                + ", lastName='" + getLastName() + '\''
                + ", username='" + getUsername() + '\''
                + ", active=" + isActive()
                + ", specialization=" + specialization
                + '}';
    }
}
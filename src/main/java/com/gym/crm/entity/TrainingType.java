package com.gym.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "training_types")
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "training_type_name",
            nullable = false,
            unique = true
    )
    private String trainingTypeName;

    protected TrainingType() {
    }

    public Long getId() {
        return id;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }

    @Override
    public String toString() {
        return "TrainingType{"
                + "id=" + id
                + ", trainingTypeName='" + trainingTypeName + '\''
                + '}';
    }
}
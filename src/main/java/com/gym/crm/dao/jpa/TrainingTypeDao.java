package com.gym.crm.dao.jpa;

import com.gym.crm.entity.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeDao {

    Optional<TrainingType> findById(Long id);

    Optional<TrainingType> findByName(String name);

    List<TrainingType> findAll();
}
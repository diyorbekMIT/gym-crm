package com.gym.crm.dao.jpa;

import java.util.List;
import com.gym.crm.entity.Trainer;

import java.util.Optional;

public interface TrainerDao {

    Trainer save(Trainer trainer);

    Optional<Trainer> findById(Long id);

    Optional<Trainer> findByUsername(String username);
    List<Trainer> findNotAssignedToTrainee(Long traineeId);
}
package com.gym.crm.dao.jpa;

import com.gym.crm.entity.Trainee;

import java.util.Optional;

public interface TraineeDao {

    Trainee save(Trainee trainee);

    Optional<Trainee> findById(Long id);

    Optional<Trainee> findByUsername(String username);

    void delete(Trainee trainee);
}
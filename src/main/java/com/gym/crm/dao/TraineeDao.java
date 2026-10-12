package com.gym.crm.dao;

import com.gym.crm.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface  TraineeDao {
    Trainee save(Trainee trainee);
    Trainee update(Trainee trainee);
    boolean deleteById(Long id);
    Optional<Trainee> findById(Long id);
    List<Trainee> findAll();
    Optional<Trainee> findByUsername(String username);
}

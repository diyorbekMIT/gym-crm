package com.gym.crm.service;

import com.gym.crm.model.Trainee;

import java.util.List;

public interface TraineeService {

    Trainee create(Trainee trainee);
    Trainee update(Trainee trainee);
    void deleteById(Long id);
    Trainee findById(Long id);
    List<Trainee> findAll();
}

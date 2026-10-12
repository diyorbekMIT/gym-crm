package com.gym.crm.service;

import com.gym.crm.model.Training;

import java.util.List;

public interface TrainingService {

    Training create(Training training);

    Training findById(Long id);

    List<Training> findAll();
}
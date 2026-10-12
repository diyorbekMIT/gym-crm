package com.gym.crm.service;

import com.gym.crm.model.Trainer;

import java.util.List;

public interface TrainerService {

    Trainer create(Trainer trainer);

    Trainer update(Trainer trainer);

    Trainer findById(Long id);

    List<Trainer> findAll();
}
package com.gym.crm.service.jpa;

import com.gym.crm.dao.jpa.TrainingTypeDao;
import com.gym.crm.entity.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TrainingTypeService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TrainingTypeService.class);

    private final TrainingTypeDao trainingTypeDao;

    public TrainingTypeService(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    public List<TrainingType> getAll() {
        List<TrainingType> trainingTypes = trainingTypeDao.findAll();

        LOGGER.info("Retrieved {} training types.", trainingTypes.size());

        return trainingTypes;
    }
}
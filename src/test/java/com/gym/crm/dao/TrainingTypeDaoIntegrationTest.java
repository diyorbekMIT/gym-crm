package com.gym.crm.dao;

import com.gym.crm.config.PersistenceConfig;
import com.gym.crm.dao.jpa.JpaTrainingTypeDao;
import com.gym.crm.dao.jpa.TrainingTypeDao;
import com.gym.crm.entity.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {
        PersistenceConfig.class,
        JpaTrainingTypeDao.class
})
@Transactional(readOnly = true)
class TrainingTypeDaoIntegrationTest {

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    @Test
    void shouldFindSeededTrainingTypes() {
        assertThat(trainingTypeDao.findAll())
                .extracting(TrainingType::getTrainingTypeName)
                .contains("Strength", "Cardio", "Functional Training");
    }

    @Test
    void shouldFindTrainingTypeByNameAndId() {
        TrainingType cardio = trainingTypeDao.findByName("Cardio")
                .orElseThrow();

        assertThat(trainingTypeDao.findById(cardio.getId()))
                .isPresent()
                .get()
                .extracting(TrainingType::getTrainingTypeName)
                .isEqualTo("Cardio");
    }

    @Test
    void shouldReturnEmptyForUnknownName() {
        assertThat(
                trainingTypeDao.findByName("__missing_training_type__")
        ).isEmpty();
    }

    @Test
    void shouldReturnEmptyForUnknownId() {
        assertThat(trainingTypeDao.findById(-1L)).isEmpty();
    }
}
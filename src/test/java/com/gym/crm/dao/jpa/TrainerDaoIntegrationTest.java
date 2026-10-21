package com.gym.crm.dao.jpa;

import com.gym.crm.config.PersistenceConfig;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(classes = {
        PersistenceConfig.class,
        JpaTrainerDao.class,
        JpaTrainingTypeDao.class
})
@Transactional
class TrainerDaoIntegrationTest {

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldPersistTrainerAndAssociatedUser() {
        Trainer trainer = newTrainer();

        trainerDao.save(trainer);
        entityManager.flush();

        Long trainerId = trainer.getId();
        Long userId = trainer.getUser().getId();

        entityManager.clear();

        Trainer loadedTrainer = trainerDao.findById(trainerId)
                .orElseThrow();

        assertThat(trainerId).isNotNull();
        assertThat(userId).isNotNull();

        assertThat(loadedTrainer.getUser().getId())
                .isEqualTo(userId);

        assertThat(loadedTrainer.getUser().getFirstName())
                .isEqualTo("Sarah");

        assertThat(
                loadedTrainer.getSpecialization().getTrainingTypeName()
        ).isEqualTo("Cardio");
    }

    @Test
    void shouldFindTrainerByUsername() {
        Trainer trainer = newTrainer();
        String username = trainer.getUser().getUsername();

        trainerDao.save(trainer);
        entityManager.flush();

        Long trainerId = trainer.getId();

        entityManager.clear();

        Trainer loadedTrainer = trainerDao.findByUsername(username)
                .orElseThrow();

        assertThat(loadedTrainer.getId()).isEqualTo(trainerId);

        assertThat(loadedTrainer.getUser().getUsername())
                .isEqualTo(username);
    }

    @Test
    void shouldNotCreateAnotherTrainingTypeWhenSavingTrainer() {
        long countBefore = countTrainingTypes();

        Trainer trainer = newTrainer();

        trainerDao.save(trainer);
        entityManager.flush();

        assertThat(countTrainingTypes()).isEqualTo(countBefore);
    }

    @Test
    void shouldReturnEmptyForUnknownUsername() {
        String username = "Missing." + UUID.randomUUID();

        assertThat(trainerDao.findByUsername(username)).isEmpty();
    }

    @Test
    void shouldReturnEmptyForUnknownId() {
        assertThat(trainerDao.findById(-1L)).isEmpty();
    }

    @Test
    void shouldRejectTrainerWithoutSpecialization() {
        Trainer trainer = newTrainer();
        trainer.setSpecialization(null);

        assertThatThrownBy(() -> {
            trainerDao.save(trainer);
            entityManager.flush();
        }).isInstanceOf(RuntimeException.class);
    }

    private Trainer newTrainer() {
        TrainingType cardio = trainingTypeDao.findByName("Cardio")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Seed training type 'Cardio' was not found."
                        )
                );

        User user = new User();
        user.setFirstName("Sarah");
        user.setLastName("Davis");
        user.setUsername("Sarah.Davis." + UUID.randomUUID());
        user.setPassword("FakePass12");
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(cardio);

        return trainer;
    }

    private long countTrainingTypes() {
        return entityManager.createQuery(
                        "SELECT COUNT(type) FROM TrainingType type",
                        Long.class
                )
                .getSingleResult();
    }
}
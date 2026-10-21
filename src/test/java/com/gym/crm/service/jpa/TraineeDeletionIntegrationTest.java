package com.gym.crm.service.jpa;

import com.gym.crm.config.PersistenceConfig;
import com.gym.crm.dao.jpa.JpaTraineeDao;
import com.gym.crm.dao.jpa.JpaTrainerDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import com.gym.crm.service.exception.AuthenticationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(classes = {
        PersistenceConfig.class,
        JpaTraineeDao.class,
        JpaTrainerDao.class,
        AuthenticationService.class,
        TraineeDeletionService.class
})
@Transactional
class TraineeDeletionIntegrationTest {

    private static final String PASSWORD = "FakePass12";

    @Autowired
    private TraineeDeletionService deletionService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldDeleteTraineeAndDependentsButKeepSharedEntities() {
        TrainingType cardio = findCardio();

        Trainer trainer = new Trainer();
        trainer.setUser(newUser("Sarah"));
        trainer.setSpecialization(cardio);

        Trainee trainee = new Trainee();
        trainee.setUser(newUser("John"));

        Trainee otherTrainee = new Trainee();
        otherTrainee.setUser(newUser("Alice"));

        entityManager.persist(trainer);
        entityManager.persist(trainee);
        entityManager.persist(otherTrainee);

        trainee.addTrainer(trainer);
        otherTrainee.addTrainer(trainer);

        Training training = newTraining(
                trainee,
                trainer,
                cardio
        );

        Training otherTraining = newTraining(
                otherTrainee,
                trainer,
                cardio
        );

        entityManager.persist(training);
        entityManager.persist(otherTraining);
        entityManager.flush();

        Long traineeId = trainee.getId();
        Long userId = trainee.getUser().getId();
        Long trainingId = training.getId();

        Long trainerId = trainer.getId();
        Long trainerUserId = trainer.getUser().getId();
        Long typeId = cardio.getId();

        Long otherTraineeId = otherTrainee.getId();
        Long otherTrainingId = otherTraining.getId();

        String username = trainee.getUser().getUsername();

        // Reload before deletion so cascade traversal uses
        // the database-backed training collection.
        entityManager.clear();

        deletionService.deleteTrainee(username, PASSWORD);

        entityManager.flush();
        entityManager.clear();

        assertThat(entityManager.find(Trainee.class, traineeId))
                .isNull();

        assertThat(entityManager.find(User.class, userId))
                .isNull();

        assertThat(entityManager.find(Training.class, trainingId))
                .isNull();

        assertThat(entityManager.find(Trainer.class, trainerId))
                .isNotNull();

        assertThat(entityManager.find(User.class, trainerUserId))
                .isNotNull();

        assertThat(entityManager.find(TrainingType.class, typeId))
                .isNotNull();

        assertThat(
                entityManager.find(Trainee.class, otherTraineeId)
        ).isNotNull();

        assertThat(
                entityManager.find(Training.class, otherTrainingId)
        ).isNotNull();

        Trainer remainingTrainer =
                entityManager.find(Trainer.class, trainerId);

        assertThat(remainingTrainer.getTrainees())
                .extracting(Trainee::getId)
                .contains(otherTraineeId)
                .doesNotContain(traineeId);

        Number remainingLinks = (Number) entityManager
                .createNativeQuery("""
                        SELECT COUNT(*)
                        FROM trainee_trainers
                        WHERE trainee_id = :traineeId
                        """)
                .setParameter("traineeId", traineeId)
                .getSingleResult();

        assertThat(remainingLinks.longValue()).isZero();
    }

    @Test
    void shouldNotDeleteTraineeWhenPasswordIsIncorrect() {
        Trainee trainee = new Trainee();
        trainee.setUser(newUser("John"));

        entityManager.persist(trainee);
        entityManager.flush();

        Long traineeId = trainee.getId();
        String username = trainee.getUser().getUsername();

        entityManager.clear();

        assertThatThrownBy(() ->
                deletionService.deleteTrainee(
                        username,
                        "WrongPassword"
                )
        ).isInstanceOf(AuthenticationException.class);

        assertThat(entityManager.find(Trainee.class, traineeId))
                .isNotNull();
    }

    private TrainingType findCardio() {
        return entityManager.createQuery(
                        """
                        SELECT type
                        FROM TrainingType type
                        WHERE type.trainingTypeName = :name
                        """,
                        TrainingType.class
                )
                .setParameter("name", "Cardio")
                .getSingleResult();
    }

    private User newUser(String firstName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName("Test");
        user.setUsername(
                firstName + ".Test." + UUID.randomUUID()
        );
        user.setPassword(PASSWORD);
        user.setActive(true);

        return user;
    }

    private Training newTraining(
            Trainee trainee,
            Trainer trainer,
            TrainingType trainingType
    ) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingName("Cardio Session");
        training.setTrainingDate(LocalDate.of(2026, 10, 22));
        training.setTrainingDuration(45);

        return training;
    }
}
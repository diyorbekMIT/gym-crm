package com.gym.crm.dao.jpa;

import com.gym.crm.entity.Training;
import com.gym.crm.config.PersistenceConfig;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
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
        JpaTrainingDao.class,
        JpaTrainingTypeDao.class
})
@Transactional
class TrainingDaoIntegrationTest {

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    @PersistenceContext
    private EntityManager entityManager;

    private Training anotherSession(
            Training reference,
            LocalDate date
    ) {
        Training training = new Training();
        training.setTrainee(reference.getTrainee());
        training.setTrainer(reference.getTrainer());
        training.setTrainingType(reference.getTrainingType());
        training.setTrainingName("Another Session");
        training.setTrainingDate(date);
        training.setTrainingDuration(60);

        return training;
    }

    @Test
    void shouldFilterByInclusiveDatesAndOrderResults() {
        Training first = newTraining();
        first.setTrainingDate(LocalDate.of(2026, 10, 1));

        Training last = anotherSession(
                first,
                LocalDate.of(2026, 10, 31)
        );

        Training outside = anotherSession(
                first,
                LocalDate.of(2026, 11, 1)
        );

        Long traineeId = first.getTrainee().getId();

        // Insert out of date order to verify query ordering.
        trainingDao.save(last);
        trainingDao.save(outside);
        trainingDao.save(first);

        entityManager.flush();
        entityManager.clear();

        assertThat(trainingDao.findForTrainee(
                traineeId,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                null,
                null
        ))
                .extracting(Training::getId)
                .containsExactly(first.getId(), last.getId());
    }

    @Test
    void shouldCombineTrainerNameAndTrainingTypeFilters() {
        Training training = newTraining();

        Long traineeId = training.getTrainee().getId();
        Long typeId = training.getTrainingType().getId();

        trainingDao.save(training);
        entityManager.flush();
        entityManager.clear();

        assertThat(trainingDao.findForTrainee(
                traineeId,
                null,
                null,
                "sArAh DaViS",
                typeId
        ))
                .extracting(Training::getId)
                .containsExactly(training.getId());

        assertThat(trainingDao.findForTrainee(
                traineeId,
                null,
                null,
                "Someone Else",
                typeId
        )).isEmpty();
    }

    @Test
    void shouldFilterTrainerTrainingsByTraineeName() {
        Training training = newTraining();
        Long trainerId = training.getTrainer().getId();

        trainingDao.save(training);
        entityManager.flush();
        entityManager.clear();

        assertThat(trainingDao.findForTrainer(
                trainerId,
                null,
                null,
                "john smith"
        ))
                .extracting(Training::getId)
                .containsExactly(training.getId());

        assertThat(trainingDao.findForTrainer(
                trainerId,
                null,
                null,
                "Someone Else"
        )).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenTrainingTypeDoesNotMatch() {
        Training training = newTraining();
        Long traineeId = training.getTrainee().getId();

        trainingDao.save(training);
        entityManager.flush();
        entityManager.clear();

        assertThat(trainingDao.findForTrainee(
                traineeId,
                null,
                null,
                null,
                -1L
        )).isEmpty();
    }

    @Test
    void shouldPersistTrainingWithExistingParticipantsAndType() {
        Training training = newTraining();

        Long traineeId = training.getTrainee().getId();
        Long trainerId = training.getTrainer().getId();
        Long typeId = training.getTrainingType().getId();

        trainingDao.save(training);
        entityManager.flush();

        Long trainingId = training.getId();

        entityManager.clear();

        Training loadedTraining = trainingDao.findById(trainingId)
                .orElseThrow();

        assertThat(trainingId).isNotNull();

        assertThat(loadedTraining.getTrainingName())
                .isEqualTo("Morning Cardio");

        assertThat(loadedTraining.getTrainingDate())
                .isEqualTo(LocalDate.of(2026, 10, 20));

        assertThat(loadedTraining.getTrainingDuration())
                .isEqualTo(45);

        assertThat(loadedTraining.getTrainee().getId())
                .isEqualTo(traineeId);

        assertThat(loadedTraining.getTrainer().getId())
                .isEqualTo(trainerId);

        assertThat(loadedTraining.getTrainingType().getId())
                .isEqualTo(typeId);

        assertThat(
                loadedTraining.getTrainee().getUser().getFirstName()
        ).isEqualTo("John");

        assertThat(
                loadedTraining.getTrainer().getUser().getFirstName()
        ).isEqualTo("Sarah");
    }

    @Test
    void shouldReturnEmptyForUnknownId() {
        assertThat(trainingDao.findById(-1L)).isEmpty();
    }

    @Test
    void shouldRejectTrainingWithoutTrainee() {
        Training training = newTraining();
        training.setTrainee(null);

        assertThatThrownBy(() -> {
            trainingDao.save(training);
            entityManager.flush();
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldRejectTrainingWithoutDate() {
        Training training = newTraining();
        training.setTrainingDate(null);

        assertThatThrownBy(() -> {
            trainingDao.save(training);
            entityManager.flush();
        }).isInstanceOf(RuntimeException.class);
    }

    private Training newTraining() {
        TrainingType cardio = trainingTypeDao.findByName("Cardio")
                .orElseThrow();

        Trainee trainee = new Trainee();
        trainee.setUser(newUser("John", "Smith"));
        trainee.setAddress("Tashkent");

        Trainer trainer = new Trainer();
        trainer.setUser(newUser("Sarah", "Davis"));
        trainer.setSpecialization(cardio);

        entityManager.persist(trainee);
        entityManager.persist(trainer);
        entityManager.flush();

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(cardio);
        training.setTrainingName("Morning Cardio");
        training.setTrainingDate(LocalDate.of(2026, 10, 20));
        training.setTrainingDuration(45);

        return training;
    }

    private User newUser(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);

        user.setUsername(
                firstName + "." + lastName + "." + UUID.randomUUID()
        );

        user.setPassword("FakePass12");
        user.setActive(true);

        return user;
    }
}
package com.gym.crm.service.jpa;

import com.gym.crm.config.PersistenceConfig;
import com.gym.crm.dao.jpa.JpaTraineeDao;
import com.gym.crm.dao.jpa.JpaTrainerDao;
import com.gym.crm.dao.jpa.JpaTrainingTypeDao;
import com.gym.crm.dao.jpa.JpaUserDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.util.JpaUsernameGenerator;
import com.gym.crm.util.PasswordGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {
        PersistenceConfig.class,
        ProfileRegistrationService.class,
        JpaTraineeDao.class,
        JpaTrainerDao.class,
        JpaTrainingTypeDao.class,
        JpaUserDao.class,
        JpaUsernameGenerator.class,
        PasswordGenerator.class
})
@Transactional
class ProfileRegistrationIntegrationTest {

    @Autowired
    private ProfileRegistrationService registrationService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldRegisterTraineeAndPersistGeneratedCredentials() {
        String lastName = uniqueLastName();

        Trainee trainee = registrationService.createTrainee(
                "John",
                lastName,
                LocalDate.of(2000, 5, 15),
                "Tashkent"
        );

        entityManager.flush();

        Long traineeId = trainee.getId();
        Long userId = trainee.getUser().getId();
        String password = trainee.getUser().getPassword();

        entityManager.clear();

        Trainee loadedTrainee =
                entityManager.find(Trainee.class, traineeId);

        assertThat(traineeId).isNotNull();
        assertThat(userId).isNotNull();

        assertThat(loadedTrainee.getUser().getUsername())
                .isEqualTo("John." + lastName);

        assertThat(loadedTrainee.getUser().getPassword())
                .isEqualTo(password)
                .matches("[A-Za-z0-9]{10}");

        assertThat(loadedTrainee.getUser().getActive()).isTrue();

        assertThat(loadedTrainee.getDateOfBirth())
                .isEqualTo(LocalDate.of(2000, 5, 15));
    }

    @Test
    void shouldRegisterTrainerWithExistingSpecialization() {
        TrainingType cardio = findCardio();
        Long specializationId = cardio.getId();

        Trainer trainer = registrationService.createTrainer(
                "Sarah",
                uniqueLastName(),
                specializationId
        );

        entityManager.flush();

        Long trainerId = trainer.getId();

        entityManager.clear();

        Trainer loadedTrainer =
                entityManager.find(Trainer.class, trainerId);

        assertThat(trainerId).isNotNull();

        assertThat(loadedTrainer.getUser().getPassword())
                .matches("[A-Za-z0-9]{10}");

        assertThat(loadedTrainer.getUser().getActive()).isTrue();

        assertThat(loadedTrainer.getSpecialization().getId())
                .isEqualTo(specializationId);
    }

    @Test
    void shouldGenerateUniqueUsernamesAcrossTraineesAndTrainers() {
        String lastName = uniqueLastName();
        Long specializationId = findCardio().getId();

        Trainee firstTrainee = registrationService.createTrainee(
                "Alex",
                lastName,
                null,
                null
        );

        Trainer trainer = registrationService.createTrainer(
                "Alex",
                lastName,
                specializationId
        );

        Trainee secondTrainee = registrationService.createTrainee(
                "Alex",
                lastName,
                null,
                null
        );

        entityManager.flush();

        String baseUsername = "Alex." + lastName;

        assertThat(firstTrainee.getUser().getUsername())
                .isEqualTo(baseUsername);

        assertThat(trainer.getUser().getUsername())
                .isEqualTo(baseUsername + "1");

        assertThat(secondTrainee.getUser().getUsername())
                .isEqualTo(baseUsername + "2");
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

    private String uniqueLastName() {
        return "Test" + UUID.randomUUID()
                .toString()
                .replace("-", "");
    }
}
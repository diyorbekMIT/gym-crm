package com.gym.crm.service.jpa;

import com.gym.crm.config.PersistenceConfig;
import com.gym.crm.dao.jpa.JpaTraineeDao;
import com.gym.crm.dao.jpa.JpaTrainerDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {
        PersistenceConfig.class,
        JpaTraineeDao.class,
        JpaTrainerDao.class,
        AuthenticationService.class,
        TrainerAssignmentService.class
})
@Transactional
class TrainerAssignmentIntegrationTest {

    private static final String PASSWORD = "FakePass12";

    @Autowired
    private TrainerAssignmentService assignmentService;

    @PersistenceContext
    private EntityManager entityManager;

    private Long traineeId;
    private Long firstTrainerId;
    private Long secondTrainerId;
    private String traineeUsername;

    @BeforeEach
    void setUp() {
        TrainingType cardio = entityManager.createQuery(
                        """
                        SELECT type
                        FROM TrainingType type
                        WHERE type.trainingTypeName = :name
                        """,
                        TrainingType.class
                )
                .setParameter("name", "Cardio")
                .getSingleResult();

        Trainee trainee = new Trainee();
        trainee.setUser(newUser("John", "Smith"));

        Trainer firstTrainer = new Trainer();
        firstTrainer.setUser(newUser("Sarah", "Davis"));
        firstTrainer.setSpecialization(cardio);

        Trainer secondTrainer = new Trainer();
        secondTrainer.setUser(newUser("Michael", "Brown"));
        secondTrainer.setSpecialization(cardio);

        entityManager.persist(trainee);
        entityManager.persist(firstTrainer);
        entityManager.persist(secondTrainer);
        entityManager.flush();

        traineeId = trainee.getId();
        traineeUsername = trainee.getUser().getUsername();
        firstTrainerId = firstTrainer.getId();
        secondTrainerId = secondTrainer.getId();

        entityManager.clear();
    }

    @Test
    void shouldPersistTrainerAssignments() {
        assignmentService.replaceAssignedTrainers(
                traineeUsername,
                PASSWORD,
                List.of(firstTrainerId, secondTrainerId)
        );

        flushAndClear();

        Trainee loadedTrainee =
                entityManager.find(Trainee.class, traineeId);

        assertThat(loadedTrainee.getTrainers())
                .extracting(Trainer::getId)
                .containsExactlyInAnyOrder(
                        firstTrainerId,
                        secondTrainerId
                );

        Trainer loadedTrainer =
                entityManager.find(Trainer.class, firstTrainerId);

        assertThat(loadedTrainer.getTrainees())
                .extracting(Trainee::getId)
                .contains(traineeId);
    }

    @Test
    void shouldExcludeAssignedTrainerFromUnassignedResults() {
        assignmentService.replaceAssignedTrainers(
                traineeUsername,
                PASSWORD,
                List.of(firstTrainerId)
        );

        flushAndClear();

        List<Trainer> unassigned =
                assignmentService.getUnassignedTrainers(
                        traineeUsername,
                        PASSWORD
                );

        assertThat(unassigned)
                .extracting(Trainer::getId)
                .contains(secondTrainerId)
                .doesNotContain(firstTrainerId);
    }

    @Test
    void shouldReplaceOldAssignmentsInsteadOfAppending() {
        assignmentService.replaceAssignedTrainers(
                traineeUsername,
                PASSWORD,
                List.of(firstTrainerId)
        );

        flushAndClear();

        assignmentService.replaceAssignedTrainers(
                traineeUsername,
                PASSWORD,
                List.of(secondTrainerId)
        );

        flushAndClear();

        Trainee loadedTrainee =
                entityManager.find(Trainee.class, traineeId);

        assertThat(loadedTrainee.getTrainers())
                .extracting(Trainer::getId)
                .containsExactly(secondTrainerId);

        Trainer previousTrainer =
                entityManager.find(Trainer.class, firstTrainerId);

        assertThat(previousTrainer.getTrainees())
                .extracting(Trainee::getId)
                .doesNotContain(traineeId);
    }

    @Test
    void shouldRemoveAssignmentsWithoutDeletingTrainers() {
        assignmentService.replaceAssignedTrainers(
                traineeUsername,
                PASSWORD,
                List.of(firstTrainerId, secondTrainerId)
        );

        flushAndClear();

        assignmentService.replaceAssignedTrainers(
                traineeUsername,
                PASSWORD,
                List.of()
        );

        flushAndClear();

        Trainee loadedTrainee =
                entityManager.find(Trainee.class, traineeId);

        assertThat(loadedTrainee.getTrainers()).isEmpty();

        assertThat(
                entityManager.find(Trainer.class, firstTrainerId)
        ).isNotNull();

        assertThat(
                entityManager.find(Trainer.class, secondTrainerId)
        ).isNotNull();

        assertThat(assignmentService.getUnassignedTrainers(
                traineeUsername,
                PASSWORD
        ))
                .extracting(Trainer::getId)
                .contains(firstTrainerId, secondTrainerId);
    }

    @Test
    void shouldPersistOnlyOneAssignmentForDuplicateIds() {
        assignmentService.replaceAssignedTrainers(
                traineeUsername,
                PASSWORD,
                List.of(firstTrainerId, firstTrainerId)
        );

        flushAndClear();

        Trainee loadedTrainee =
                entityManager.find(Trainee.class, traineeId);

        assertThat(loadedTrainee.getTrainers())
                .extracting(Trainer::getId)
                .containsExactly(firstTrainerId);
    }

    private User newUser(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(
                firstName + "." + lastName + "." + UUID.randomUUID()
        );
        user.setPassword(PASSWORD);
        user.setActive(true);

        return user;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
package com.gym.crm.dao.jpa;

import com.gym.crm.config.PersistenceConfig;
import com.gym.crm.entity.Trainee;
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

@SpringJUnitConfig(classes = {
        PersistenceConfig.class,
        JpaTraineeDao.class
})
@Transactional
class TraineeDaoIntegrationTest {

    @Autowired
    private TraineeDao traineeDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldPersistTraineeAndAssociatedUser() {
        Trainee trainee = newTrainee();

        traineeDao.save(trainee);
        entityManager.flush();

        Long traineeId = trainee.getId();
        Long userId = trainee.getUser().getId();

        entityManager.clear();

        Trainee loadedTrainee = traineeDao.findById(traineeId)
                .orElseThrow();

        assertThat(traineeId).isNotNull();
        assertThat(userId).isNotNull();

        assertThat(loadedTrainee.getUser().getId())
                .isEqualTo(userId);

        assertThat(loadedTrainee.getUser().getFirstName())
                .isEqualTo("John");

        assertThat(loadedTrainee.getDateOfBirth())
                .isEqualTo(LocalDate.of(2000, 5, 15));

        assertThat(loadedTrainee.getAddress())
                .isEqualTo("Tashkent");
    }

    @Test
    void shouldFindTraineeByUsername() {
        Trainee trainee = newTrainee();
        String username = trainee.getUser().getUsername();

        traineeDao.save(trainee);
        entityManager.flush();
        entityManager.clear();

        Trainee loadedTrainee = traineeDao.findByUsername(username)
                .orElseThrow();

        assertThat(loadedTrainee.getId())
                .isEqualTo(trainee.getId());

        assertThat(loadedTrainee.getUser().getUsername())
                .isEqualTo(username);
    }

    @Test
    void shouldReturnEmptyForUnknownUsername() {
        String username = "Missing." + UUID.randomUUID();

        assertThat(traineeDao.findByUsername(username)).isEmpty();
    }

    @Test
    void shouldReturnEmptyForUnknownId() {
        assertThat(traineeDao.findById(-1L)).isEmpty();
    }

    @Test
    void shouldPersistTraineeWithoutOptionalFields() {
        Trainee trainee = newTrainee();
        trainee.setDateOfBirth(null);
        trainee.setAddress(null);

        traineeDao.save(trainee);
        entityManager.flush();

        Long id = trainee.getId();

        entityManager.clear();

        Trainee loadedTrainee = traineeDao.findById(id)
                .orElseThrow();

        assertThat(loadedTrainee.getDateOfBirth()).isNull();
        assertThat(loadedTrainee.getAddress()).isNull();
    }

    private Trainee newTrainee() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setUsername("John.Smith." + UUID.randomUUID());
        user.setPassword("FakePass12");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(2000, 5, 15));
        trainee.setAddress("Tashkent");

        return trainee;
    }
}
package com.gym.crm.service.jpa;

import com.gym.crm.config.HibernateAppConfig;
import com.gym.crm.dao.jpa.TraineeDao;
import com.gym.crm.dao.jpa.TrainerDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.service.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(HibernateAppConfig.class)
@Transactional
class ProfileManagementServiceTest {

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private ProfileRegistrationService registrationService;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainerDao trainerDao;

    @Test
    void shouldUpdateTraineeProfile() {
        Trainee trainee = registrationService.createTrainee(
                "John",
                "Smith",
                LocalDate.of(1995, 5, 10),
                "123 Main Street"
        );

        String username = trainee.getUser().getUsername();
        String password = trainee.getUser().getPassword();

        Trainee updated = profileManagementService.updateTrainee(
                username,
                password,
                "UpdatedJohn",
                "UpdatedSmith",
                LocalDate.of(1996, 6, 20),
                "456 New Street"
        );

        assertThat(updated.getUser().getFirstName())
                .isEqualTo("UpdatedJohn");
        assertThat(updated.getUser().getLastName())
                .isEqualTo("UpdatedSmith");
        assertThat(updated.getDateOfBirth())
                .isEqualTo(LocalDate.of(1996, 6, 20));
        assertThat(updated.getAddress())
                .isEqualTo("456 New Street");

        assertThat(traineeDao.findById(updated.getId())).isPresent();
    }

    @Test
    void shouldUpdateTrainerSpecialization() {
        Trainer trainer = registrationService.createTrainer(
                "Alice",
                "Brown",
                1L
        );

        String username = trainer.getUser().getUsername();
        String password = trainer.getUser().getPassword();

        Trainer updated = profileManagementService.updateTrainer(
                username,
                password,
                "UpdatedAlice",
                "UpdatedBrown",
                2L
        );

        assertThat(updated.getUser().getFirstName())
                .isEqualTo("UpdatedAlice");
        assertThat(updated.getSpecialization().getId())
                .isEqualTo(2L);

        assertThat(trainerDao.findById(updated.getId())).isPresent();
    }

    @Test
    void shouldDeactivateAndActivateTrainee() {
        Trainee trainee = registrationService.createTrainee(
                "Mark",
                "Taylor",
                LocalDate.of(1990, 1, 1),
                "789 Street"
        );

        String username = trainee.getUser().getUsername();
        String password = trainee.getUser().getPassword();

        profileManagementService.deactivateTrainee(username, password);

        Trainee deactivated = traineeDao
                .findByUsername(username)
                .orElseThrow();

        assertThat(deactivated.getUser().getActive()).isFalse();

        profileManagementService.activateTrainee(username, password);

        Trainee activated = traineeDao
                .findByUsername(username)
                .orElseThrow();

        assertThat(activated.getUser().getActive()).isTrue();
    }

    @Test
    void shouldRejectInvalidTrainerSpecialization() {
        Trainer trainer = registrationService.createTrainer(
                "Anna",
                "White",
                1L
        );

        String username = trainer.getUser().getUsername();
        String password = trainer.getUser().getPassword();

        assertThatThrownBy(() -> profileManagementService.updateTrainer(
                username,
                password,
                "Anna",
                "White",
                999999L
        ))
                .isInstanceOf(EntityNotFoundException.class);

        Trainer unchanged = trainerDao
                .findByUsername(username)
                .orElseThrow();

        assertThat(unchanged.getSpecialization().getId()).isEqualTo(1L);
    }
}
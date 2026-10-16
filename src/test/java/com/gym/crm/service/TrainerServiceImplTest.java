package com.gym.crm.service;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.service.exception.EntityNotFoundException;
import com.gym.crm.util.PasswordGenerator;
import com.gym.crm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrainerServiceImplTest {

    private TrainerDao trainerDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class);
        usernameGenerator = mock(UsernameGenerator.class);
        passwordGenerator = mock(PasswordGenerator.class);

        trainerService = new TrainerServiceImpl(trainerDao, usernameGenerator, passwordGenerator);
    }

    // ==================== create ====================

    @Test
    void shouldGenerateCredentialsAndActivateTrainerBeforeSaving() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Sarah");
        trainer.setLastName("Davis");
        trainer.setSpecialization(new TrainingType("Cardio"));

        when(usernameGenerator.generateUsername("Sarah", "Davis"))
                .thenReturn("Sarah.Davis");

        when(passwordGenerator.generatePassword())
                .thenReturn("Abc123Xyz9");

        when(trainerDao.save(trainer))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.create(trainer);

        assertThat(result.getUsername()).isEqualTo("Sarah.Davis");
        assertThat(result.getPassword()).isEqualTo("Abc123Xyz9");
        assertThat(result.isActive()).isTrue();

        verify(trainerDao).save(trainer);
    }

    @Test
    void shouldRejectNullTrainerOnCreate() {
        assertThatThrownBy(() -> trainerService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer must not be null.");
    }

    @Test
    void shouldRejectTrainerWithBlankFirstName() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("");
        trainer.setLastName("Davis");
        trainer.setSpecialization(new TrainingType("Cardio"));

        assertThatThrownBy(() -> trainerService.create(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer first name must not be blank.");
    }

    @Test
    void shouldRejectTrainerWithBlankLastName() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Sarah");
        trainer.setLastName("  ");
        trainer.setSpecialization(new TrainingType("Cardio"));

        assertThatThrownBy(() -> trainerService.create(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer last name must not be blank.");
    }

    @Test
    void shouldRejectTrainerWithNullSpecialization() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Sarah");
        trainer.setLastName("Davis");
        trainer.setSpecialization(null);

        assertThatThrownBy(() -> trainerService.create(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer specialization must not be null.");
    }

    // ==================== update ====================

    @Test
    void shouldUpdateExistingTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUserId(1L);
        trainer.setFirstName("Updated");

        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainerDao.update(trainer)).thenReturn(trainer);

        Trainer result = trainerService.update(trainer);

        verify(trainerDao).update(trainer);
        assertThat(result.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void shouldRejectNullTrainerOnUpdate() {
        assertThatThrownBy(() -> trainerService.update(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer and trainer ID must not be null for update.");
    }

    @Test
    void shouldRejectTrainerWithNullIdOnUpdate() {
        Trainer trainer = new Trainer();

        assertThatThrownBy(() -> trainerService.update(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer and trainer ID must not be null for update.");
    }

    @Test
    void shouldThrowEntityNotFoundWhenUpdatingNonExistentTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUserId(99L);

        when(trainerDao.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.update(trainer))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Trainer with id=99 was not found.");
    }

    // ==================== findById ====================

    @Test
    void shouldReturnTrainerWhenFoundById() {
        Trainer trainer = new Trainer();
        trainer.setUserId(1L);

        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.findById(1L);

        assertThat(result).isSameAs(trainer);
    }

    @Test
    void shouldRejectNullIdOnFindById() {
        assertThatThrownBy(() -> trainerService.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer ID must be a positive number.");
    }

    @Test
    void shouldThrowEntityNotFoundWhenTrainerNotFound() {
        when(trainerDao.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Trainer with id=99 was not found.");
    }
}
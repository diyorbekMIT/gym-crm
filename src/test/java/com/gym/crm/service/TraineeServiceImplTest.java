package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.service.exception.EntityNotFoundException;
import com.gym.crm.util.PasswordGenerator;
import com.gym.crm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TraineeServiceImplTest {

    private TraineeDao traineeDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        usernameGenerator = mock(UsernameGenerator.class);
        passwordGenerator = mock(PasswordGenerator.class);

        traineeService = new TraineeServiceImpl(traineeDao, usernameGenerator, passwordGenerator);
    }

    // ==================== create ====================

    @Test
    void shouldGenerateCredentialsAndActivateTraineeBeforeSaving() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Smith");

        when(usernameGenerator.generateUsername("John", "Smith"))
                .thenReturn("John.Smith");

        when(passwordGenerator.generatePassword())
                .thenReturn("abc123XYZ9");

        when(traineeDao.save(trainee))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Trainee result = traineeService.create(trainee);

        ArgumentCaptor<Trainee> traineeCaptor =
                ArgumentCaptor.forClass(Trainee.class);

        verify(traineeDao).save(traineeCaptor.capture());

        Trainee savedTrainee = traineeCaptor.getValue();

        assertThat(savedTrainee.getUsername()).isEqualTo("John.Smith");
        assertThat(savedTrainee.getPassword()).isEqualTo("abc123XYZ9");
        assertThat(savedTrainee.isActive()).isTrue();
        assertThat(result).isSameAs(trainee);
    }

    @Test
    void shouldRejectNullTraineeOnCreate() {
        assertThatThrownBy(() -> traineeService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee must not be null.");
    }

    @Test
    void shouldRejectTraineeWithBlankFirstName() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("  ");
        trainee.setLastName("Smith");

        assertThatThrownBy(() -> traineeService.create(trainee))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee first name must not be blank.");
    }

    @Test
    void shouldRejectTraineeWithBlankLastName() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("");

        assertThatThrownBy(() -> traineeService.create(trainee))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee last name must not be blank.");
    }

    // ==================== update ====================

    @Test
    void shouldUpdateExistingTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
        trainee.setFirstName("Updated");

        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee result = traineeService.update(trainee);

        verify(traineeDao).update(trainee);
        assertThat(result.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void shouldRejectNullTraineeOnUpdate() {
        assertThatThrownBy(() -> traineeService.update(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee and trainee ID must not be null for update.");
    }

    @Test
    void shouldRejectTraineeWithNullIdOnUpdate() {
        Trainee trainee = new Trainee();

        assertThatThrownBy(() -> traineeService.update(trainee))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee and trainee ID must not be null for update.");
    }

    @Test
    void shouldThrowEntityNotFoundWhenUpdatingNonExistentTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUserId(99L);

        when(traineeDao.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.update(trainee))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Trainee with id=99 was not found.");
    }

    // ==================== deleteById ====================

    @Test
    void shouldDeleteExistingTrainee() {
        when(traineeDao.deleteById(1L)).thenReturn(true);

        traineeService.deleteById(1L);

        verify(traineeDao).deleteById(1L);
    }

    @Test
    void shouldRejectNullIdOnDelete() {
        assertThatThrownBy(() -> traineeService.deleteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee ID must be a positive number.");
    }

    @Test
    void shouldRejectNegativeIdOnDelete() {
        assertThatThrownBy(() -> traineeService.deleteById(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee ID must be a positive number.");
    }

    @Test
    void shouldThrowEntityNotFoundWhenDeletingNonExistentTrainee() {
        when(traineeDao.deleteById(99L)).thenReturn(false);

        assertThatThrownBy(() -> traineeService.deleteById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Trainee with id=99 was not found.");
    }

    // ==================== findById ====================

    @Test
    void shouldReturnTraineeWhenFoundById() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);

        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.findById(1L);

        assertThat(result).isSameAs(trainee);
    }

    @Test
    void shouldRejectNullIdOnFindById() {
        assertThatThrownBy(() -> traineeService.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee ID must be a positive number.");
    }

    @Test
    void shouldThrowEntityNotFoundWhenTraineeNotFound() {
        when(traineeDao.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Trainee with id=99 was not found.");
    }
}
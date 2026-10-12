package com.gym.crm.util;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UsernameGeneratorTest {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private UsernameGenerator usernameGenerator;

    @BeforeEach
    void setUp() {
        traineeDao = Mockito.mock(TraineeDao.class);
        trainerDao = Mockito.mock(TrainerDao.class);

        usernameGenerator = new UsernameGenerator();
        usernameGenerator.setTraineeDao(traineeDao);
        usernameGenerator.setTrainerDao(trainerDao);

        when(traineeDao.findByUsername(anyString())).thenReturn(Optional.empty());
        when(trainerDao.findByUsername(anyString())).thenReturn(Optional.empty());
    }

    @Test
    void shouldGenerateBaseUsernameWhenItDoesNotExist() {
        String username = usernameGenerator.generateUsername("John", "Smith");

        assertThat(username).isEqualTo("John.Smith");
    }

    @Test
    void shouldAddOneWhenBaseUsernameAlreadyExistsForTrainee() {
        when(traineeDao.findByUsername("John.Smith"))
                .thenReturn(Optional.of(new Trainee()));

        String username = usernameGenerator.generateUsername("John", "Smith");

        assertThat(username).isEqualTo("John.Smith1");
    }

    @Test
    void shouldCheckTrainerAndTraineeNamespacesForCollisions() {
        when(trainerDao.findByUsername("John.Smith"))
                .thenReturn(Optional.of(new Trainer()));

        String username = usernameGenerator.generateUsername("John", "Smith");

        assertThat(username).isEqualTo("John.Smith1");
    }

    @Test
    void shouldIncrementSuffixUntilAnAvailableUsernameIsFound() {
        when(traineeDao.findByUsername("John.Smith"))
                .thenReturn(Optional.of(new Trainee()));

        when(trainerDao.findByUsername("John.Smith1"))
                .thenReturn(Optional.of(new Trainer()));

        when(traineeDao.findByUsername("John.Smith2"))
                .thenReturn(Optional.of(new Trainee()));

        String username = usernameGenerator.generateUsername("John", "Smith");

        assertThat(username).isEqualTo("John.Smith3");
    }

    @Test
    void shouldRejectBlankFirstName() {
        assertThatThrownBy(() -> usernameGenerator.generateUsername(" ", "Smith"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name must not be blank.");
    }

    @Test
    void shouldRejectBlankLastName() {
        assertThatThrownBy(() -> usernameGenerator.generateUsername("John", " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Last name must not be blank.");
    }
}
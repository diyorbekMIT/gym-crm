package com.gym.crm.service.jpa;

import com.gym.crm.dao.jpa.TraineeDao;
import com.gym.crm.dao.jpa.TrainerDao;
import com.gym.crm.dao.jpa.TrainingTypeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.service.exception.EntityNotFoundException;
import com.gym.crm.util.UsernameGenerator;
import com.gym.crm.util.PasswordGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ProfileRegistrationServiceTest {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingTypeDao trainingTypeDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    private ProfileRegistrationService registrationService;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);
        trainingTypeDao = mock(TrainingTypeDao.class);
        usernameGenerator = mock(UsernameGenerator.class);
        passwordGenerator = mock(PasswordGenerator.class);

        registrationService = new ProfileRegistrationService(
                traineeDao,
                trainerDao,
                trainingTypeDao,
                usernameGenerator,
                passwordGenerator
        );
    }

    @Test
    void shouldCreateTraineeWithGeneratedCredentials() {
        when(usernameGenerator.generateUsername("John", "Smith"))
                .thenReturn("John.Smith");

        when(passwordGenerator.generatePassword())
                .thenReturn("FakePass12");

        when(traineeDao.save(any(Trainee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Trainee trainee = registrationService.createTrainee(
                "John",
                "Smith",
                LocalDate.of(2000, 5, 15),
                "Tashkent"
        );

        assertThat(trainee.getUser().getUsername())
                .isEqualTo("John.Smith");

        assertThat(trainee.getUser().getPassword())
                .isEqualTo("FakePass12");

        assertThat(trainee.getUser().getActive()).isTrue();

        assertThat(trainee.getDateOfBirth())
                .isEqualTo(LocalDate.of(2000, 5, 15));

        assertThat(trainee.getAddress()).isEqualTo("Tashkent");
    }

    @Test
    void shouldCreateTrainerWithExistingSpecialization() {
        TrainingType specialization = mock(TrainingType.class);

        when(trainingTypeDao.findById(1L))
                .thenReturn(Optional.of(specialization));

        when(usernameGenerator.generateUsername("Sarah", "Davis"))
                .thenReturn("Sarah.Davis");

        when(passwordGenerator.generatePassword())
                .thenReturn("FakePass12");

        when(trainerDao.save(any(Trainer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Trainer trainer = registrationService.createTrainer(
                "Sarah",
                "Davis",
                1L
        );

        assertThat(trainer.getSpecialization())
                .isSameAs(specialization);

        assertThat(trainer.getUser().getUsername())
                .isEqualTo("Sarah.Davis");

        assertThat(trainer.getUser().getPassword())
                .isEqualTo("FakePass12");

        assertThat(trainer.getUser().getActive()).isTrue();
    }

    @Test
    void shouldRejectInvalidSpecializationIdBeforeDatabaseLookup() {
        assertThatThrownBy(() ->
                registrationService.createTrainer(
                        "Sarah",
                        "Davis",
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Specialization ID must be a positive number."
                );

        verifyNoInteractions(
                trainingTypeDao,
                trainerDao,
                usernameGenerator,
                passwordGenerator
        );
    }

    @Test
    void shouldRejectUnknownSpecializationWithoutSavingTrainer() {
        when(trainingTypeDao.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                registrationService.createTrainer(
                        "Sarah",
                        "Davis",
                        999L
                )
        )
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(
                        "Training type with id=999 was not found."
                );

        verifyNoInteractions(
                trainerDao,
                usernameGenerator,
                passwordGenerator
        );
    }

    @Test
    void shouldNotSaveTraineeWhenNameValidationFails() {
        when(usernameGenerator.generateUsername(" ", "Smith"))
                .thenThrow(new IllegalArgumentException(
                        "First name must not be blank."
                ));

        assertThatThrownBy(() ->
                registrationService.createTrainee(
                        " ",
                        "Smith",
                        null,
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name must not be blank.");

        verifyNoInteractions(traineeDao, passwordGenerator);
    }
}
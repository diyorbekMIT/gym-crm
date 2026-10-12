package com.gym.crm.service;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.util.PasswordGenerator;
import com.gym.crm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
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

        trainerService = new TrainerServiceImpl();
        trainerService.setTrainerDao(trainerDao);
        trainerService.setUsernameGenerator(usernameGenerator);
        trainerService.setPasswordGenerator(passwordGenerator);
    }

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
}
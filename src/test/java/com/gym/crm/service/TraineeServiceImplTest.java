package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.util.PasswordGenerator;
import com.gym.crm.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
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

        traineeService = new TraineeServiceImpl();
        traineeService.setTraineeDao(traineeDao);
        traineeService.setUsernameGenerator(usernameGenerator);
        traineeService.setPasswordGenerator(passwordGenerator);
    }

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
}
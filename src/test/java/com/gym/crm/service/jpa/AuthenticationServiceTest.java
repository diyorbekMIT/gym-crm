package com.gym.crm.service.jpa;

import com.gym.crm.dao.jpa.TraineeDao;
import com.gym.crm.dao.jpa.TrainerDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.User;
import com.gym.crm.service.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);

        authenticationService = new AuthenticationService(
                traineeDao,
                trainerDao
        );
    }

    @Test
    void shouldAuthenticateTraineeWithMatchingCredentials() {
        Trainee trainee = new Trainee();
        trainee.setUser(newUser("John.Smith", "FakePass12"));

        when(traineeDao.findByUsername("John.Smith"))
                .thenReturn(Optional.of(trainee));

        Trainee result = authenticationService.authenticateTrainee(
                "John.Smith",
                "FakePass12"
        );

        assertThat(result).isSameAs(trainee);
    }

    @Test
    void shouldAuthenticateTrainerWithMatchingCredentials() {
        Trainer trainer = new Trainer();
        trainer.setUser(newUser("Sarah.Davis", "FakePass12"));

        when(trainerDao.findByUsername("Sarah.Davis"))
                .thenReturn(Optional.of(trainer));

        Trainer result = authenticationService.authenticateTrainer(
                "Sarah.Davis",
                "FakePass12"
        );

        assertThat(result).isSameAs(trainer);
    }

    @Test
    void shouldRejectUnknownTrainee() {
        when(traineeDao.findByUsername("Missing.User"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authenticationService.authenticateTrainee(
                        "Missing.User",
                        "FakePass12"
                )
        )
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password.");
    }

    @Test
    void shouldRejectIncorrectTrainerPassword() {
        Trainer trainer = new Trainer();
        trainer.setUser(newUser("Sarah.Davis", "FakePass12"));

        when(trainerDao.findByUsername("Sarah.Davis"))
                .thenReturn(Optional.of(trainer));

        assertThatThrownBy(() ->
                authenticationService.authenticateTrainer(
                        "Sarah.Davis",
                        "WrongPass1"
                )
        )
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password.");
    }

    @Test
    void shouldRejectBlankUsernameWithoutQueryingDatabase() {
        assertThatThrownBy(() ->
                authenticationService.authenticateTrainee(
                        " ",
                        "FakePass12"
                )
        ).isInstanceOf(AuthenticationException.class);

        verifyNoInteractions(traineeDao, trainerDao);
    }

    @Test
    void shouldRejectNullPasswordWithoutQueryingDatabase() {
        assertThatThrownBy(() ->
                authenticationService.authenticateTrainer(
                        "Sarah.Davis",
                        null
                )
        ).isInstanceOf(AuthenticationException.class);

        verifyNoInteractions(traineeDao, trainerDao);
    }

    private User newUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        return user;
    }
}
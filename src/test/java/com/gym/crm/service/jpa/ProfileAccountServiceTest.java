package com.gym.crm.service.jpa;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.User;
import com.gym.crm.service.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProfileAccountServiceTest {

    private AuthenticationService authenticationService;
    private ProfileAccountService accountService;

    @BeforeEach
    void setUp() {
        authenticationService = mock(AuthenticationService.class);

        accountService = new ProfileAccountService(
                authenticationService
        );
    }

    @Test
    void shouldReturnAuthenticatedTraineeProfile() {
        Trainee trainee = new Trainee();

        when(authenticationService.authenticateTrainee(
                "John.Smith",
                "OldPass123"
        )).thenReturn(trainee);

        Trainee result = accountService.getTraineeProfile(
                "John.Smith",
                "OldPass123"
        );

        assertThat(result).isSameAs(trainee);
    }

    @Test
    void shouldReturnAuthenticatedTrainerProfile() {
        Trainer trainer = new Trainer();

        when(authenticationService.authenticateTrainer(
                "Sarah.Davis",
                "OldPass123"
        )).thenReturn(trainer);

        Trainer result = accountService.getTrainerProfile(
                "Sarah.Davis",
                "OldPass123"
        );

        assertThat(result).isSameAs(trainer);
    }

    @Test
    void shouldChangeTraineePasswordAfterAuthentication() {
        Trainee trainee = new Trainee();
        trainee.setUser(newUser());

        when(authenticationService.authenticateTrainee(
                "John.Smith",
                "OldPass123"
        )).thenReturn(trainee);

        accountService.changeTraineePassword(
                "John.Smith",
                "OldPass123",
                "NewPass456"
        );

        assertThat(trainee.getUser().getPassword())
                .isEqualTo("NewPass456");
    }

    @Test
    void shouldChangeTrainerPasswordAfterAuthentication() {
        Trainer trainer = new Trainer();
        trainer.setUser(newUser());

        when(authenticationService.authenticateTrainer(
                "Sarah.Davis",
                "OldPass123"
        )).thenReturn(trainer);

        accountService.changeTrainerPassword(
                "Sarah.Davis",
                "OldPass123",
                "NewPass456"
        );

        assertThat(trainer.getUser().getPassword())
                .isEqualTo("NewPass456");
    }

    @Test
    void shouldRejectPasswordChangeWhenAuthenticationFails() {
        when(authenticationService.authenticateTrainee(
                "John.Smith",
                "WrongPassword"
        )).thenThrow(new AuthenticationException());

        assertThatThrownBy(() ->
                accountService.changeTraineePassword(
                        "John.Smith",
                        "WrongPassword",
                        "NewPass456"
                )
        ).isInstanceOf(AuthenticationException.class);
    }

    @Test
    void shouldKeepPasswordUnchangedWhenNewPasswordIsBlank() {
        Trainer trainer = new Trainer();
        trainer.setUser(newUser());

        when(authenticationService.authenticateTrainer(
                "Sarah.Davis",
                "OldPass123"
        )).thenReturn(trainer);

        assertThatThrownBy(() ->
                accountService.changeTrainerPassword(
                        "Sarah.Davis",
                        "OldPass123",
                        " "
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("New password must not be blank.");

        assertThat(trainer.getUser().getPassword())
                .isEqualTo("OldPass123");
    }

    private User newUser() {
        User user = new User();
        user.setPassword("OldPass123");
        return user;
    }
}
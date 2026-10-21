package com.gym.crm.service.jpa;

import com.gym.crm.dao.jpa.TrainingDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.service.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TrainingQueryServiceTest {

    private AuthenticationService authenticationService;
    private TrainingDao trainingDao;
    private TrainingQueryService queryService;

    @BeforeEach
    void setUp() {
        authenticationService = mock(AuthenticationService.class);
        trainingDao = mock(TrainingDao.class);

        queryService = new TrainingQueryService(
                authenticationService,
                trainingDao
        );
    }

    @Test
    void shouldUseAuthenticatedTraineeIdAndForwardFilters() {
        Trainee trainee = mock(Trainee.class);
        when(trainee.getId()).thenReturn(7L);

        when(authenticationService.authenticateTrainee(
                "John.Smith",
                "FakePass12"
        )).thenReturn(trainee);

        LocalDate from = LocalDate.of(2026, 10, 1);
        LocalDate to = LocalDate.of(2026, 10, 31);

        List<Training> expected = List.of(new Training());

        when(trainingDao.findForTrainee(
                7L, from, to, "Sarah Davis", 3L
        )).thenReturn(expected);

        List<Training> result = queryService.getTraineeTrainings(
                "John.Smith",
                "FakePass12",
                from,
                to,
                "Sarah Davis",
                3L
        );

        assertThat(result).isSameAs(expected);

        verify(trainingDao).findForTrainee(
                7L, from, to, "Sarah Davis", 3L
        );
    }

    @Test
    void shouldUseAuthenticatedTrainerIdWithNoFilters() {
        Trainer trainer = mock(Trainer.class);
        when(trainer.getId()).thenReturn(8L);

        when(authenticationService.authenticateTrainer(
                "Sarah.Davis",
                "FakePass12"
        )).thenReturn(trainer);

        when(trainingDao.findForTrainer(
                8L, null, null, null
        )).thenReturn(List.of());

        assertThat(queryService.getTrainerTrainings(
                "Sarah.Davis",
                "FakePass12",
                null,
                null,
                null
        )).isEmpty();

        verify(trainingDao).findForTrainer(
                8L, null, null, null
        );
    }

    @Test
    void shouldRejectInvalidDateRangeBeforeQueryingTrainings() {
        when(authenticationService.authenticateTrainee(
                "John.Smith",
                "FakePass12"
        )).thenReturn(new Trainee());

        assertThatThrownBy(() ->
                queryService.getTraineeTrainings(
                        "John.Smith",
                        "FakePass12",
                        LocalDate.of(2026, 10, 31),
                        LocalDate.of(2026, 10, 1),
                        null,
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("From date must not be after to date.");

        verifyNoInteractions(trainingDao);
    }

    @Test
    void shouldRejectUnauthenticatedRequestsBeforeQueryingTrainings() {
        when(authenticationService.authenticateTrainer(
                "Sarah.Davis",
                "WrongPassword"
        )).thenThrow(new AuthenticationException());

        assertThatThrownBy(() ->
                queryService.getTrainerTrainings(
                        "Sarah.Davis",
                        "WrongPassword",
                        null,
                        null,
                        null
                )
        ).isInstanceOf(AuthenticationException.class);

        verifyNoInteractions(trainingDao);
    }
}
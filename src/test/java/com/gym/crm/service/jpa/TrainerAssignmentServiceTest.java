package com.gym.crm.service.jpa;

import com.gym.crm.dao.jpa.TrainerDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.service.exception.AuthenticationException;
import com.gym.crm.service.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TrainerAssignmentServiceTest {

    private AuthenticationService authenticationService;
    private TrainerDao trainerDao;
    private TrainerAssignmentService assignmentService;

    @BeforeEach
    void setUp() {
        authenticationService = mock(AuthenticationService.class);
        trainerDao = mock(TrainerDao.class);

        assignmentService = new TrainerAssignmentService(
                authenticationService,
                trainerDao
        );
    }

    @Test
    void shouldReplaceExistingAssignments() {
        Trainee trainee = authenticatedTrainee();

        Trainer oldTrainer = new Trainer();
        Trainer newTrainer = new Trainer();

        trainee.addTrainer(oldTrainer);

        when(trainerDao.findById(2L))
                .thenReturn(Optional.of(newTrainer));

        List<Trainer> result =
                assignmentService.replaceAssignedTrainers(
                        "John.Smith",
                        "FakePass12",
                        List.of(2L)
                );

        assertThat(result).containsExactly(newTrainer);

        assertThat(trainee.getTrainers())
                .containsExactly(newTrainer);

        assertThat(oldTrainer.getTrainees()).isEmpty();

        assertThat(newTrainer.getTrainees())
                .containsExactly(trainee);
    }

    @Test
    void shouldRemoveAllAssignmentsForEmptyList() {
        Trainee trainee = authenticatedTrainee();
        Trainer trainer = new Trainer();

        trainee.addTrainer(trainer);

        assignmentService.replaceAssignedTrainers(
                "John.Smith",
                "FakePass12",
                List.of()
        );

        assertThat(trainee.getTrainers()).isEmpty();
        assertThat(trainer.getTrainees()).isEmpty();

        verifyNoInteractions(trainerDao);
    }

    @Test
    void shouldResolveDuplicateTrainerIdsOnlyOnce() {
        Trainee trainee = authenticatedTrainee();
        Trainer trainer = new Trainer();

        when(trainerDao.findById(2L))
                .thenReturn(Optional.of(trainer));

        assignmentService.replaceAssignedTrainers(
                "John.Smith",
                "FakePass12",
                List.of(2L, 2L)
        );

        assertThat(trainee.getTrainers()).containsExactly(trainer);

        verify(trainerDao, times(1)).findById(2L);
    }

    @Test
    void shouldKeepAssignmentsWhenAnyTrainerIsMissing() {
        Trainee trainee = authenticatedTrainee();

        Trainer oldTrainer = new Trainer();
        trainee.addTrainer(oldTrainer);

        when(trainerDao.findById(2L))
                .thenReturn(Optional.of(new Trainer()));

        when(trainerDao.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                assignmentService.replaceAssignedTrainers(
                        "John.Smith",
                        "FakePass12",
                        List.of(2L, 999L)
                )
        ).isInstanceOf(EntityNotFoundException.class);

        assertThat(trainee.getTrainers())
                .containsExactly(oldTrainer);

        assertThat(oldTrainer.getTrainees())
                .containsExactly(trainee);
    }

    @Test
    void shouldRejectNullListBeforeLookingUpTrainers() {
        authenticatedTrainee();

        assertThatThrownBy(() ->
                assignmentService.replaceAssignedTrainers(
                        "John.Smith",
                        "FakePass12",
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer IDs must not be null.");

        verifyNoInteractions(trainerDao);
    }

    @Test
    void shouldRejectUnauthenticatedAssignmentChanges() {
        when(authenticationService.authenticateTrainee(
                "John.Smith",
                "FakePass12"
        )).thenThrow(new AuthenticationException());

        assertThatThrownBy(() ->
                assignmentService.replaceAssignedTrainers(
                        "John.Smith",
                        "FakePass12",
                        List.of(2L)
                )
        ).isInstanceOf(AuthenticationException.class);

        verifyNoInteractions(trainerDao);
    }

    @Test
    void shouldFindUnassignedTrainersForAuthenticatedTrainee() {
        Trainee trainee = mock(Trainee.class);
        Trainer trainer = new Trainer();

        when(trainee.getId()).thenReturn(7L);

        when(authenticationService.authenticateTrainee(
                "John.Smith",
                "FakePass12"
        )).thenReturn(trainee);

        when(trainerDao.findNotAssignedToTrainee(7L))
                .thenReturn(List.of(trainer));

        assertThat(assignmentService.getUnassignedTrainers(
                "John.Smith",
                "FakePass12"
        )).containsExactly(trainer);
    }

    private Trainee authenticatedTrainee() {
        Trainee trainee = new Trainee();

        when(authenticationService.authenticateTrainee(
                "John.Smith",
                "FakePass12"
        )).thenReturn(trainee);

        return trainee;
    }
}
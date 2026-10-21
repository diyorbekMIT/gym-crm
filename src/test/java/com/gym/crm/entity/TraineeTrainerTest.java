package com.gym.crm.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TraineeTrainerTest {

    @Test
    void shouldMaintainBothSidesWhenAssigningTrainer() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();

        trainee.addTrainer(trainer);

        assertThat(trainee.getTrainers()).containsExactly(trainer);
        assertThat(trainer.getTrainees()).containsExactly(trainee);
    }

    @Test
    void shouldMaintainBothSidesWhenRemovingTrainer() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();

        trainee.addTrainer(trainer);
        trainee.removeTrainer(trainer);

        assertThat(trainee.getTrainers()).isEmpty();
        assertThat(trainer.getTrainees()).isEmpty();
    }

    @Test
    void shouldNotAssignSameTrainerTwice() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();

        trainee.addTrainer(trainer);
        trainee.addTrainer(trainer);

        assertThat(trainee.getTrainers()).hasSize(1);
        assertThat(trainer.getTrainees()).hasSize(1);
    }

    @Test
    void shouldRejectNullTrainer() {
        Trainee trainee = new Trainee();

        assertThatThrownBy(() -> trainee.addTrainer(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer must not be null.");
    }
}
package com.gym.crm.dao.jpa;

import com.gym.crm.entity.Trainer;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaTrainerDao implements TrainerDao {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(JpaTrainerDao.class);

    private final EntityManager entityManager;

    public JpaTrainerDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Trainer save(Trainer trainer) {
        entityManager.persist(trainer);

        LOGGER.debug("Persisted trainer with id={}.", trainer.getId());

        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        LOGGER.debug("Finding trainer by id={}.", id);

        return entityManager.createQuery(
                        """
                        SELECT trainer
                        FROM Trainer trainer
                        JOIN FETCH trainer.user
                        JOIN FETCH trainer.specialization
                        WHERE trainer.id = :id
                        """,
                        Trainer.class
                )
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        LOGGER.debug("Finding trainer by username='{}'.", username);

        return entityManager.createQuery(
                        """
                        SELECT trainer
                        FROM Trainer trainer
                        JOIN FETCH trainer.user trainerUser
                        JOIN FETCH trainer.specialization
                        WHERE trainerUser.username = :username
                        """,
                        Trainer.class
                )
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Trainer> findNotAssignedToTrainee(Long traineeId) {
        LOGGER.debug(
                "Finding trainers not assigned to trainee id={}.",
                traineeId
        );

        return entityManager.createQuery(
                        """
                        SELECT trainer
                        FROM Trainer trainer
                        JOIN FETCH trainer.user trainerUser
                        JOIN FETCH trainer.specialization
                        WHERE NOT EXISTS (
                            SELECT trainee.id
                            FROM Trainee trainee
                            JOIN trainee.trainers assignedTrainer
                            WHERE trainee.id = :traineeId
                              AND assignedTrainer.id = trainer.id
                        )
                        ORDER BY trainerUser.username
                        """,
                        Trainer.class
                )
                .setParameter("traineeId", traineeId)
                .getResultList();
    }
}
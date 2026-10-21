package com.gym.crm.dao.jpa;

import com.gym.crm.entity.Trainee;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaTraineeDao implements TraineeDao {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(JpaTraineeDao.class);

    private final EntityManager entityManager;

    public JpaTraineeDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Trainee save(Trainee trainee) {
        entityManager.persist(trainee);

        LOGGER.debug("Persisted trainee with id={}.", trainee.getId());

        return trainee;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        LOGGER.debug("Finding trainee by id={}.", id);

        return entityManager.createQuery(
                        """
                        SELECT trainee
                        FROM Trainee trainee
                        JOIN FETCH trainee.user
                        WHERE trainee.id = :id
                        """,
                        Trainee.class
                )
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        LOGGER.debug("Finding trainee by username='{}'.", username);

        return entityManager.createQuery(
                        """
                        SELECT trainee
                        FROM Trainee trainee
                        JOIN FETCH trainee.user user
                        WHERE user.username = :username
                        """,
                        Trainee.class
                )
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    public void delete(Trainee trainee) {
        entityManager.remove(trainee);

        LOGGER.debug(
                "Scheduled deletion of trainee id={}.",
                trainee.getId()
        );
    }
}
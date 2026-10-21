package com.gym.crm.dao.jpa;

import com.gym.crm.entity.TrainingType;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaTrainingTypeDao implements TrainingTypeDao {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(JpaTrainingTypeDao.class);

    private final EntityManager entityManager;

    public JpaTrainingTypeDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<TrainingType> findById(Long id) {
        LOGGER.debug("Finding training type by id={}.", id);

        return Optional.ofNullable(
                entityManager.find(TrainingType.class, id)
        );
    }

    @Override
    public Optional<TrainingType> findByName(String name) {
        LOGGER.debug("Finding training type by name='{}'.", name);

        return entityManager.createQuery(
                        """
                        SELECT type
                        FROM TrainingType type
                        WHERE type.trainingTypeName = :name
                        """,
                        TrainingType.class
                )
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<TrainingType> findAll() {
        LOGGER.debug("Finding all training types.");

        return entityManager.createQuery(
                        """
                        SELECT type
                        FROM TrainingType type
                        ORDER BY type.trainingTypeName
                        """,
                        TrainingType.class
                )
                .getResultList();
    }
}
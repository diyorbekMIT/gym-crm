package com.gym.crm.dao.jpa;

import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.gym.crm.entity.Training;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaTrainingDao implements TrainingDao {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(JpaTrainingDao.class);

    private final EntityManager entityManager;

    public JpaTrainingDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Training save(Training training) {
        entityManager.persist(training);

        LOGGER.debug(
                "Persisted training with id={}.",
                training.getId()
        );

        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        LOGGER.debug("Finding training by id={}.", id);

        return entityManager.createQuery(
                        """
                        SELECT training
                        FROM Training training
                        JOIN FETCH training.trainee trainee
                        JOIN FETCH trainee.user
                        JOIN FETCH training.trainer trainer
                        JOIN FETCH trainer.user
                        JOIN FETCH training.trainingType
                        WHERE training.id = :id
                        """,
                        Training.class
                )
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }


    public List<Training> findForTrainee(
            Long traineeId,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            Long trainingTypeId
    ) {
        StringBuilder hql = new StringBuilder("""
            SELECT training
            FROM Training training
            JOIN FETCH training.trainee trainee
            JOIN FETCH trainee.user
            JOIN FETCH training.trainer trainer
            JOIN FETCH trainer.user trainerUser
            JOIN FETCH training.trainingType trainingType
            WHERE trainee.id = :participantId
            """);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("participantId", traineeId);

        addDateFilters(hql, parameters, fromDate, toDate);

        if (trainerName != null && !trainerName.isBlank()) {
            hql.append("""
                 AND LOWER(CONCAT(
                     trainerUser.firstName, ' ', trainerUser.lastName
                 )) = :participantName
                """);

            parameters.put(
                    "participantName",
                    trainerName.trim().toLowerCase(Locale.ROOT)
            );
        }

        if (trainingTypeId != null) {
            hql.append(" AND trainingType.id = :trainingTypeId");

            parameters.put("trainingTypeId", trainingTypeId);
        }

        return executeTrainingQuery(hql, parameters);
    }


    public List<Training> findForTrainer(
            Long trainerId,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        StringBuilder hql = new StringBuilder("""
            SELECT training
            FROM Training training
            JOIN FETCH training.trainee trainee
            JOIN FETCH trainee.user traineeUser
            JOIN FETCH training.trainer trainer
            JOIN FETCH trainer.user
            JOIN FETCH training.trainingType
            WHERE trainer.id = :participantId
            """);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("participantId", trainerId);

        addDateFilters(hql, parameters, fromDate, toDate);

        if (traineeName != null && !traineeName.isBlank()) {
            hql.append("""
                 AND LOWER(CONCAT(
                     traineeUser.firstName, ' ', traineeUser.lastName
                 )) = :participantName
                """);

            parameters.put(
                    "participantName",
                    traineeName.trim().toLowerCase(Locale.ROOT)
            );
        }

        return executeTrainingQuery(hql, parameters);
    }

    private void addDateFilters(
            StringBuilder hql,
            Map<String, Object> parameters,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        if (fromDate != null) {
            hql.append(" AND training.trainingDate >= :fromDate");
            parameters.put("fromDate", fromDate);
        }

        if (toDate != null) {
            hql.append(" AND training.trainingDate <= :toDate");
            parameters.put("toDate", toDate);
        }
    }

    private List<Training> executeTrainingQuery(
            StringBuilder hql,
            Map<String, Object> parameters
    ) {
        hql.append(" ORDER BY training.trainingDate ASC, training.id ASC");

        TypedQuery<Training> query = entityManager.createQuery(
                hql.toString(),
                Training.class
        );

        parameters.forEach((name, value) ->
                query.setParameter(name, value)
        );

        return query.getResultList();
    }


}
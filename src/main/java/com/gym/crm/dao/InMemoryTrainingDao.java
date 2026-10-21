package com.gym.crm.dao;

import com.gym.crm.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTrainingDao implements TrainingDao {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(InMemoryTrainingDao.class);

    private Map<Long, Training> trainingStorage;
    private final AtomicLong idSequence = new AtomicLong(0);

    @Autowired
    public void setTrainingStorage(
            @Qualifier("trainingStorage") Map<Long, Training> trainingStorage
    ) {
        this.trainingStorage = trainingStorage;

        long highestExistingId = trainingStorage.keySet()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        idSequence.set(highestExistingId);

        LOGGER.debug(
                "Training storage injected. Next generated training ID will be {}.",
                highestExistingId + 1
        );
    }

    @Override
    public Training save(Training training) {
        Long id = training.getId();

        if (id == null) {
            id = idSequence.incrementAndGet();
            training.setId(id);
        } else {
            long providedId = id;
            idSequence.updateAndGet(current -> Math.max(current, providedId));
        }

        trainingStorage.put(id, training);

        LOGGER.debug("Saved training with id={}.", id);

        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        LOGGER.debug("Finding training by id={}.", id);

        return Optional.ofNullable(trainingStorage.get(id));
    }

    @Override
    public List<Training> findAll() {
        LOGGER.debug("Finding all trainings. Count={}.", trainingStorage.size());

        return trainingStorage.values()
                .stream()
                .sorted(Comparator.comparing(Training::getId))
                .toList();
    }

    @Override
    public List<Training> findForTrainee(
            Long traineeId,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            Long trainingTypeId
    ) {
        LOGGER.debug("Finding trainings for traineeId={}.", traineeId);

        return trainingStorage.values().stream()
                .filter(t -> traineeId.equals(t.getTraineeId()))
                .filter(t -> fromDate == null
                        || !t.getTrainingDate().isBefore(fromDate))
                .filter(t -> toDate == null
                        || !t.getTrainingDate().isAfter(toDate))
                .filter(t -> trainerName == null || trainerName.isBlank()
                        || trainerName.equals(String.valueOf(t.getTrainerId())))
                .filter(t -> trainingTypeId == null)
                .sorted(Comparator.comparing(Training::getTrainingDate))
                .toList();
    }

    @Override
    public List<Training> findForTrainer(
            Long trainerId,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        LOGGER.debug("Finding trainings for trainerId={}.", trainerId);

        return trainingStorage.values().stream()
                .filter(t -> trainerId.equals(t.getTrainerId()))
                .filter(t -> fromDate == null
                        || !t.getTrainingDate().isBefore(fromDate))
                .filter(t -> toDate == null
                        || !t.getTrainingDate().isAfter(toDate))
                .filter(t -> traineeName == null || traineeName.isBlank()
                        || traineeName.equals(String.valueOf(t.getTraineeId())))
                .sorted(Comparator.comparing(Training::getTrainingDate))
                .toList();
    }
}
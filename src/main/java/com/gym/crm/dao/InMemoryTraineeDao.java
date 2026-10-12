package com.gym.crm.dao;

import com.gym.crm.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTraineeDao implements TraineeDao {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(InMemoryTraineeDao.class);

    private Map<Long, Trainee> traineeStorage;
    private final AtomicLong idSequence = new AtomicLong(0);

    @Autowired
    public void setTraineeStorage(@Qualifier("traineeStorage") Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;

        long highestExistingId = traineeStorage.keySet()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        idSequence.set(highestExistingId);

        LOGGER.debug(
                "Trainee storage injected. Next generated trainee ID will be {}.",
                highestExistingId + 1
        );
    }

    @Override
    public Trainee save(Trainee trainee) {
        Long id = trainee.getUserId();

        if (id == null) {
            id = idSequence.incrementAndGet();
            trainee.setUserId(id);
        } else {
            long providedId = id;
            idSequence.updateAndGet(current -> Math.max(current, providedId));
        }

        traineeStorage.put(id, trainee);

        LOGGER.debug("Saved trainee with id={}.", id);

        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        Long id = trainee.getUserId();

        if (id == null || !traineeStorage.containsKey(id)) {
            throw new IllegalArgumentException(
                    "Cannot update trainee. Trainee with id=" + id + " does not exist."
            );
        }

        traineeStorage.put(id, trainee);

        LOGGER.debug("Updated trainee with id={}.", id);

        return trainee;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleted = traineeStorage.remove(id) != null;

        LOGGER.debug("Delete trainee with id={}: {}.", id, deleted);

        return deleted;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        LOGGER.debug("Finding trainee by id={}.", id);

        return Optional.ofNullable(traineeStorage.get(id));
    }

    @Override
    public List<Trainee> findAll() {
        LOGGER.debug("Finding all trainees. Count={}.", traineeStorage.size());

        return traineeStorage.values()
                .stream()
                .sorted(Comparator.comparing(Trainee::getUserId))
                .toList();
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        LOGGER.debug("Finding trainee by username='{}'.", username);

        return traineeStorage.values()
                .stream()
                .filter(trainee -> username.equals(trainee.getUsername()))
                .findFirst();
    }
}
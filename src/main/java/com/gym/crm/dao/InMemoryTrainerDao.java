package com.gym.crm.dao;

import com.gym.crm.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTrainerDao implements TrainerDao {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(InMemoryTrainerDao.class);

    private Map<Long, Trainer> trainerStorage;
    private final AtomicLong idSequence = new AtomicLong(0);

    @Autowired
    public void setTrainerStorage(
            @Qualifier("trainerStorage") Map<Long, Trainer> trainerStorage
    ) {
        this.trainerStorage = trainerStorage;

        long highestExistingId = trainerStorage.keySet()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        idSequence.set(highestExistingId);

        LOGGER.debug(
                "Trainer storage injected. Next generated trainer ID will be {}.",
                highestExistingId + 1
        );
    }

    @Override
    public Trainer save(Trainer trainer) {
        Long id = trainer.getUserId();

        if (id == null) {
            id = idSequence.incrementAndGet();
            trainer.setUserId(id);
        } else {
            long providedId = id;
            idSequence.updateAndGet(current -> Math.max(current, providedId));
        }

        trainerStorage.put(id, trainer);

        LOGGER.debug("Saved trainer with id={}.", id);

        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        Long id = trainer.getUserId();

        trainerStorage.put(id, trainer);

        LOGGER.debug("Updated trainer with id={}.", id);

        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        LOGGER.debug("Finding trainer by id={}.", id);

        return Optional.ofNullable(trainerStorage.get(id));
    }

    @Override
    public List<Trainer> findAll() {
        LOGGER.debug("Finding all trainers. Count={}.", trainerStorage.size());

        return trainerStorage.values()
                .stream()
                .sorted(Comparator.comparing(Trainer::getUserId))
                .toList();
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        LOGGER.debug("Finding trainer by username='{}'.", username);

        return trainerStorage.values()
                .stream()
                .filter(trainer -> username.equals(trainer.getUsername()))
                .findFirst();
    }
}
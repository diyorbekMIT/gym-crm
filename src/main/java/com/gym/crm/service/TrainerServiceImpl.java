package com.gym.crm.service;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainer;
import com.gym.crm.service.exception.EntityNotFoundException;
import com.gym.crm.util.PasswordGenerator;
import com.gym.crm.util.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerDao trainerDao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;


    @Autowired
    public TrainerServiceImpl(
            TrainerDao trainerDao,
            @Qualifier("inMemoryUsernameGenerator") UsernameGenerator usernameGenerator,
            PasswordGenerator passwordGenerator
    ) {
        this.trainerDao = trainerDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    @Override
    public Trainer create(Trainer trainer) {
        validateTrainerForCreation(trainer);

        trainer.setUsername(
                usernameGenerator.generateUsername(
                        trainer.getFirstName(),
                        trainer.getLastName()
                )
        );

        trainer.setPassword(passwordGenerator.generatePassword());
        trainer.setActive(true);

        Trainer savedTrainer = trainerDao.save(trainer);

        LOGGER.info(
                "Created trainer: id={}, username='{}'.",
                savedTrainer.getUserId(),
                savedTrainer.getUsername()
        );

        return savedTrainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        if (trainer == null || trainer.getUserId() == null) {
            throw new IllegalArgumentException(
                    "Trainer and trainer ID must not be null for update."
            );
        }

        if (trainerDao.findById(trainer.getUserId()).isEmpty()) {
            LOGGER.warn("Cannot update trainer. id={} was not found.", trainer.getUserId());

            throw new EntityNotFoundException(
                    "Trainer with id=" + trainer.getUserId() + " was not found."
            );
        }

        Trainer updatedTrainer = trainerDao.update(trainer);

        LOGGER.info(
                "Updated trainer: id={}, username='{}'.",
                updatedTrainer.getUserId(),
                updatedTrainer.getUsername()
        );

        return updatedTrainer;
    }

    @Override
    public Trainer findById(Long id) {
        validateId(id);

        return trainerDao.findById(id)
                .orElseThrow(() -> {
                    LOGGER.warn("Trainer with id={} was not found.", id);

                    return new EntityNotFoundException(
                            "Trainer with id=" + id + " was not found."
                    );
                });
    }

    @Override
    public List<Trainer> findAll() {
        List<Trainer> trainers = trainerDao.findAll();

        LOGGER.info("Selected {} trainer(s).", trainers.size());

        return trainers;
    }

    private void validateTrainerForCreation(Trainer trainer) {
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer must not be null.");
        }

        if (trainer.getFirstName() == null || trainer.getFirstName().isBlank()) {
            throw new IllegalArgumentException("Trainer first name must not be blank.");
        }

        if (trainer.getLastName() == null || trainer.getLastName().isBlank()) {
            throw new IllegalArgumentException("Trainer last name must not be blank.");
        }

        if (trainer.getSpecialization() == null) {
            throw new IllegalArgumentException("Trainer specialization must not be null.");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Trainer ID must be a positive number.");
        }
    }
}
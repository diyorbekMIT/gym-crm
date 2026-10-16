package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.service.exception.EntityNotFoundException;
import com.gym.crm.util.PasswordGenerator;
import com.gym.crm.util.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeDao traineeDao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    @Autowired
    public TraineeServiceImpl(
            TraineeDao traineeDao,
            UsernameGenerator usernameGenerator,
            PasswordGenerator passwordGenerator
    ) {
        this.traineeDao = traineeDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    @Override
    public Trainee create(Trainee trainee) {
        validateTraineeForCreation(trainee);

        trainee.setUsername(
                usernameGenerator.generateUsername(
                        trainee.getFirstName(),
                        trainee.getLastName()
                )
        );

        trainee.setPassword(passwordGenerator.generatePassword());
        trainee.setActive(true);

        Trainee savedTrainee = traineeDao.save(trainee);

        LOGGER.info(
                "Created trainee: id={}, username='{}'.",
                savedTrainee.getUserId(),
                savedTrainee.getUsername()
        );

        return savedTrainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        if (trainee == null || trainee.getUserId() == null) {
            throw new IllegalArgumentException(
                    "Trainee and trainee ID must not be null for update."
            );
        }

        if (traineeDao.findById(trainee.getUserId()).isEmpty()) {
            LOGGER.warn("Cannot update trainee. id={} was not found.", trainee.getUserId());

            throw new EntityNotFoundException(
                    "Trainee with id=" + trainee.getUserId() + " was not found."
            );
        }

        Trainee updatedTrainee = traineeDao.update(trainee);

        LOGGER.info(
                "Updated trainee: id={}, username='{}'.",
                updatedTrainee.getUserId(),
                updatedTrainee.getUsername()
        );

        return updatedTrainee;
    }

    @Override
    public void deleteById(Long id) {
        validateId(id, "Trainee");

        boolean deleted = traineeDao.deleteById(id);

        if (!deleted) {
            LOGGER.warn("Cannot delete trainee. id={} was not found.", id);

            throw new EntityNotFoundException(
                    "Trainee with id=" + id + " was not found."
            );
        }

        LOGGER.info("Deleted trainee with id={}.", id);
    }

    @Override
    public Trainee findById(Long id) {
        validateId(id, "Trainee");

        return traineeDao.findById(id)
                .orElseThrow(() -> {
                    LOGGER.warn("Trainee with id={} was not found.", id);

                    return new EntityNotFoundException(
                            "Trainee with id=" + id + " was not found."
                    );
                });
    }

    @Override
    public List<Trainee> findAll() {
        List<Trainee> trainees = traineeDao.findAll();

        LOGGER.info("Selected {} trainee(s).", trainees.size());

        return trainees;
    }

    private void validateTraineeForCreation(Trainee trainee) {
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee must not be null.");
        }

        if (trainee.getFirstName() == null || trainee.getFirstName().isBlank()) {
            throw new IllegalArgumentException("Trainee first name must not be blank.");
        }

        if (trainee.getLastName() == null || trainee.getLastName().isBlank()) {
            throw new IllegalArgumentException("Trainee last name must not be blank.");
        }
    }

    private void validateId(Long id, String entityName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    entityName + " ID must be a positive number."
            );
        }
    }
}
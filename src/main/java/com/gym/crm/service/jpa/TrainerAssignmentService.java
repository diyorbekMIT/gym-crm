package com.gym.crm.service.jpa;

import com.gym.crm.dao.jpa.TrainerDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.service.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class TrainerAssignmentService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TrainerAssignmentService.class);

    private final AuthenticationService authenticationService;
    private final TrainerDao trainerDao;

    public TrainerAssignmentService(
            AuthenticationService authenticationService,
            TrainerDao trainerDao
    ) {
        this.authenticationService = authenticationService;
        this.trainerDao = trainerDao;
    }

    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainers(
            String username,
            String password
    ) {
        Trainee trainee = authenticationService.authenticateTrainee(
                username,
                password
        );

        return trainerDao.findNotAssignedToTrainee(
                trainee.getId()
        );
    }

    @Transactional
    public List<Trainer> replaceAssignedTrainers(
            String username,
            String password,
            List<Long> trainerIds
    ) {
        Trainee trainee = authenticationService.authenticateTrainee(
                username,
                password
        );

        Set<Long> uniqueIds = validateTrainerIds(trainerIds);

        List<Trainer> requestedTrainers = new ArrayList<>();

        // Resolve every trainer before changing any assignments.
        for (Long trainerId : uniqueIds) {
            Trainer trainer = trainerDao.findById(trainerId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Trainer with id="
                                    + trainerId
                                    + " was not found."
                    ));

            requestedTrainers.add(trainer);
        }

        // Copy before removal to avoid modifying a collection
        // while iterating over it.
        for (Trainer existing :
                new ArrayList<>(trainee.getTrainers())) {

            trainee.removeTrainer(existing);
        }

        for (Trainer requested : requestedTrainers) {
            trainee.addTrainer(requested);
        }

        LOGGER.info(
                "Replaced trainer assignments for trainee id={}; count={}.",
                trainee.getId(),
                requestedTrainers.size()
        );

        return List.copyOf(requestedTrainers);
    }

    private Set<Long> validateTrainerIds(List<Long> trainerIds) {
        if (trainerIds == null) {
            throw new IllegalArgumentException(
                    "Trainer IDs must not be null."
            );
        }

        Set<Long> uniqueIds = new LinkedHashSet<>();

        for (Long trainerId : trainerIds) {
            if (trainerId == null || trainerId <= 0) {
                throw new IllegalArgumentException(
                        "Every trainer ID must be a positive number."
                );
            }

            uniqueIds.add(trainerId);
        }

        return uniqueIds;
    }
}
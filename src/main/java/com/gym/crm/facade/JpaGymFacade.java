package com.gym.crm.facade;

import com.gym.crm.dao.jpa.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.service.jpa.ProfileAccountService;
import com.gym.crm.service.jpa.ProfileManagementService;
import com.gym.crm.service.jpa.ProfileRegistrationService;
import com.gym.crm.service.jpa.TrainerAssignmentService;
import com.gym.crm.service.jpa.TraineeDeletionService;
import com.gym.crm.service.jpa.TrainingManagementService;
import com.gym.crm.service.jpa.TrainingQueryService;
import org.springframework.stereotype.Component;
import com.gym.crm.dao.jpa.TrainerDao;

import java.time.LocalDate;
import java.util.List;
import com.gym.crm.service.exception.AccessDeniedException;
import com.gym.crm.dao.jpa.TrainerDao;
import com.gym.crm.service.exception.EntityNotFoundException;
import java.util.List;

@Component
public class JpaGymFacade {

    private final ProfileRegistrationService registrationService;
    private final ProfileAccountService accountService;
    private final ProfileManagementService managementService;
    private final TrainingManagementService trainingService;
    private final TrainingQueryService queryService;
    private final TrainerAssignmentService assignmentService;
    private final TraineeDeletionService deletionService;
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;

    public JpaGymFacade(
            ProfileRegistrationService registrationService,
            ProfileAccountService accountService,
            ProfileManagementService managementService,
            TrainingManagementService trainingService,
            TrainingQueryService queryService,
            TrainerAssignmentService assignmentService,
            TraineeDeletionService deletionService,
            TraineeDao traineeDao,
            TrainerDao trainerDao
    ) {
        this.registrationService = registrationService;
        this.accountService = accountService;
        this.managementService = managementService;
        this.trainingService = trainingService;
        this.queryService = queryService;
        this.assignmentService = assignmentService;
        this.deletionService = deletionService;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    public Trainee createTrainee(
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String address
    ) {
        return registrationService.createTrainee(
                firstName, lastName, dateOfBirth, address
        );
    }

    public Trainer createTrainer(
            String firstName,
            String lastName,
            Long specializationId
    ) {
        return registrationService.createTrainer(
                firstName, lastName, specializationId
        );
    }

    public Trainee updateTraineeProfile(
            String username,
            String password,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String address,
            boolean isActive
    ) {
        Trainee trainee = managementService.updateTrainee(
                username,
                password,
                firstName,
                lastName,
                dateOfBirth,
                address
        );

        if (Boolean.TRUE.equals(trainee.getUser().getActive()) != isActive) {
            if (isActive) {
                managementService.activateTrainee(username, password);
            } else {
                managementService.deactivateTrainee(username, password);
            }
        }

        return accountService.getTraineeProfile(username, password);
    }

    public List<Trainer> getUnassignedTrainersByUsername(
            String username,
            String password
    ) {
        return assignmentService.getUnassignedTrainers(
                username,
                password
        );
    }

    public List<Trainer> replaceAssignedTrainersByUsernames(
            String username,
            String password,
            List<String> trainerUsernames
    ) {
        List<Long> trainerIds = trainerUsernames.stream()
                .map(this::findTrainerIdByUsername)
                .toList();

        return assignmentService.replaceAssignedTrainers(
                username,
                password,
                trainerIds
        );
    }

    private Long findTrainerIdByUsername(String trainerUsername) {
        return trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Trainer with username="
                                + trainerUsername
                                + " was not found."
                ))
                .getId();
    }




    

    public Trainee getTraineeProfile(
            String username,
            String password
    ) {
        return accountService.getTraineeProfile(username, password);
    }

    public Trainer getTrainerProfile(
            String username,
            String password
    ) {
        return accountService.getTrainerProfile(username, password);
    }

    public void changeTraineePassword(
            String username,
            String currentPassword,
            String newPassword
    ) {
        accountService.changeTraineePassword(
                username, currentPassword, newPassword
        );
    }

    public void changeTrainerPassword(
            String username,
            String currentPassword,
            String newPassword
    ) {
        accountService.changeTrainerPassword(
                username, currentPassword, newPassword
        );
    }

    public Trainee updateTrainee(
            String username,
            String password,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String address
    ) {
        return managementService.updateTrainee(
                username, password, firstName, lastName,
                dateOfBirth, address
        );
    }

    public Trainer updateTrainer(
            String username,
            String password,
            String firstName,
            String lastName,
            Long specializationId
    ) {
        return managementService.updateTrainer(
                username, password, firstName, lastName,
                specializationId
        );
    }

    public Trainer getTrainerProfileByUsername(
            String authenticatedUsername,
            String password,
            String requestedUsername
    ) {
        if (!authenticatedUsername.equalsIgnoreCase(requestedUsername)) {
            throw new AccessDeniedException(
                    "You can access only your own profile."
            );
        }

        return accountService.getTrainerProfile(
                authenticatedUsername,
                password
        );
    }

    public Trainer updateTrainerProfile(
            String username,
            String password,
            String firstName,
            String lastName,
            boolean isActive
    ) {
        Trainer currentTrainer = accountService.getTrainerProfile(username, password);

        Trainer trainer = managementService.updateTrainer(
                username,
                password,
                firstName,
                lastName,
                currentTrainer.getSpecialization().getId()
        );

        if (Boolean.TRUE.equals(trainer.getUser().getActive()) != isActive) {
            if (isActive) {
                managementService.activateTrainer(username, password);
            } else {
                managementService.deactivateTrainer(username, password);
            }
        }

        return accountService.getTrainerProfile(username, password);
    }



    public void activateTrainee(String username, String password) {
        managementService.activateTrainee(username, password);
    }

    public void deactivateTrainee(String username, String password) {
        managementService.deactivateTrainee(username, password);
    }

    public void activateTrainer(String username, String password) {
        managementService.activateTrainer(username, password);
    }

    public void deactivateTrainer(String username, String password) {
        managementService.deactivateTrainer(username, password);
    }

    public Training addTrainingByUsernames(
            String authenticatedUsername,
            String password,
            String traineeUsername,
            String trainerUsername,
            String trainingName,
            Long trainingTypeId,
            LocalDate trainingDate,
            Integer trainingDuration
    ) {
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Trainee with username="
                                + traineeUsername
                                + " was not found."
                ));

        Trainer trainer = trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Trainer with username="
                                + trainerUsername
                                + " was not found."
                ));

        return trainingService.addTraining(
                authenticatedUsername,
                password,
                trainer.getId(),
                trainingName,
                trainingTypeId,
                trainingDate,
                trainingDuration
        );
    }





    public Training addTraining(
            String username,
            String password,
            Long trainerId,
            String trainingName,
            Long trainingTypeId,
            LocalDate date,
            Integer durationMinutes
    ) {
        return trainingService.addTraining(
                username, password, trainerId, trainingName,
                trainingTypeId, date, durationMinutes
        );
    }

    public List<Training> getTraineeTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            Long trainingTypeId
    ) {
        return queryService.getTraineeTrainings(
                username, password, fromDate, toDate,
                trainerName, trainingTypeId
        );
    }

    public List<Training> getTrainerTrainings(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        return queryService.getTrainerTrainings(
                username, password, fromDate, toDate, traineeName
        );
    }

    public List<Trainer> getUnassignedTrainers(
            String username,
            String password
    ) {
        return assignmentService.getUnassignedTrainers(
                username, password
        );
    }

    public List<Trainer> replaceAssignedTrainers(
            String username,
            String password,
            List<Long> trainerIds
    ) {
        return assignmentService.replaceAssignedTrainers(
                username, password, trainerIds
        );
    }

    public void deleteTrainee(
            String username,
            String password
    ) {
        deletionService.deleteTrainee(username, password);
    }

    public Trainee getTraineeProfileByUsername(
            String authenticatedUsername,
            String password,
            String requestedUsername
    ) {
        if (!authenticatedUsername.equalsIgnoreCase(requestedUsername)) {
            throw new AccessDeniedException(
                    "You can access only your own profile."
            );
        }

        return accountService.getTraineeProfile(
                authenticatedUsername,
                password
        );
    }
}
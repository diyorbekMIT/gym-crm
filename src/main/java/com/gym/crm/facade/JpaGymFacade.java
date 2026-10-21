package com.gym.crm.facade;

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

import java.time.LocalDate;
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

    public JpaGymFacade(
            ProfileRegistrationService registrationService,
            ProfileAccountService accountService,
            ProfileManagementService managementService,
            TrainingManagementService trainingService,
            TrainingQueryService queryService,
            TrainerAssignmentService assignmentService,
            TraineeDeletionService deletionService
    ) {
        this.registrationService = registrationService;
        this.accountService = accountService;
        this.managementService = managementService;
        this.trainingService = trainingService;
        this.queryService = queryService;
        this.assignmentService = assignmentService;
        this.deletionService = deletionService;
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
}
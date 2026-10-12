package com.gym.crm;

import com.gym.crm.config.AppConfig;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class GymCrmApplication {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            GymFacade gymFacade = context.getBean(GymFacade.class);

            printInitialData(gymFacade);

            Trainee trainee = createTrainee(gymFacade);
            Trainer trainer = createTrainer(gymFacade);
            Training training = createTraining(
                    gymFacade,
                    trainee.getUserId(),
                    trainer.getUserId()
            );

            printCreatedEntities(trainee, trainer, training);
            printFinalData(gymFacade);
        }
    }

    private static void printInitialData(GymFacade gymFacade) {
        System.out.println("\n========== INITIAL DATA ==========");

        System.out.println("\nTrainees:");
        gymFacade.getAllTrainees().forEach(System.out::println);

        System.out.println("\nTrainers:");
        gymFacade.getAllTrainers().forEach(System.out::println);

        System.out.println("\nTrainings:");
        gymFacade.getAllTrainings().forEach(System.out::println);
    }

    private static Trainee createTrainee(GymFacade gymFacade) {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bekzod");
        trainee.setLastName("Karimov");
        trainee.setDateOfBirth(LocalDate.of(2002, 7, 12));
        trainee.setAddress("Tashkent, Uzbekistan");

        return gymFacade.createTrainee(trainee);
    }

    private static Trainer createTrainer(GymFacade gymFacade) {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Dilshod");
        trainer.setLastName("Rakhimov");
        trainer.setSpecialization(new TrainingType("Functional Training"));

        return gymFacade.createTrainer(trainer);
    }

    private static Training createTraining(
            GymFacade gymFacade,
            Long traineeId,
            Long trainerId
    ) {
        Training training = new Training();
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        training.setTrainingName("Functional Training Session");
        training.setTrainingType(new TrainingType("Functional Training"));
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);

        return gymFacade.createTraining(training);
    }

    private static void printCreatedEntities(
            Trainee trainee,
            Trainer trainer,
            Training training
    ) {
        System.out.println("\n========== CREATED ENTITIES ==========");

        System.out.println("\nCreated trainee:");
        System.out.println(trainee);

        System.out.println("\nCreated trainer:");
        System.out.println(trainer);

        System.out.println("\nCreated training:");
        System.out.println(training);
    }

    private static void printFinalData(GymFacade gymFacade) {
        System.out.println("\n========== FINAL COUNTS ==========");

        System.out.println("Trainees: " + gymFacade.getAllTrainees().size());
        System.out.println("Trainers: " + gymFacade.getAllTrainers().size());
        System.out.println("Trainings: " + gymFacade.getAllTrainings().size());
    }
}
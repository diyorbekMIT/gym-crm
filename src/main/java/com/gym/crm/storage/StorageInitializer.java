package com.gym.crm.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class StorageInitializer implements BeanPostProcessor {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(StorageInitializer.class);

    private String initializationFile;

    private List<Trainee> trainees = Collections.emptyList();
    private List<Trainer> trainers = Collections.emptyList();
    private List<Training> trainings = Collections.emptyList();

    private boolean dataLoaded;

    @Value("${storage.init.file}")
    public void setInitializationFile(String initializationFile) {
        this.initializationFile = initializationFile;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        loadDataOnce();

        switch (beanName) {
            case "traineeStorage" -> loadTrainees(bean);
            case "trainerStorage" -> loadTrainers(bean);
            case "trainingStorage" -> loadTrainings(bean);
            default -> {
                // This post-processor ignores every bean except the 3 storage maps.
            }
        }

        return bean;
    }

    private void loadDataOnce() {
        if (dataLoaded) {
            return;
        }

        dataLoaded = true;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try (InputStream inputStream =
                     new ClassPathResource(initializationFile).getInputStream()) {

            JsonNode root = objectMapper.readTree(inputStream);

            trainees = objectMapper.convertValue(
                    root.path("trainees"),
                    new TypeReference<List<Trainee>>() {
                    }
            );

            trainers = objectMapper.convertValue(
                    root.path("trainers"),
                    new TypeReference<List<Trainer>>() {
                    }
            );

            trainings = objectMapper.convertValue(
                    root.path("trainings"),
                    new TypeReference<List<Training>>() {
                    }
            );

            LOGGER.info(
                    "Loaded seed data from '{}': {} trainees, {} trainers, {} trainings",
                    initializationFile,
                    trainees.size(),
                    trainers.size(),
                    trainings.size()
            );

        } catch (IOException | IllegalArgumentException exception) {
            LOGGER.error(
                    "Could not load storage seed data from '{}'. Application starts with empty storage.",
                    initializationFile,
                    exception
            );

            trainees = Collections.emptyList();
            trainers = Collections.emptyList();
            trainings = Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTrainees(Object bean) {
        if (!(bean instanceof Map<?, ?>)) {
            LOGGER.warn("Bean 'traineeStorage' is not a Map. Seed data was not loaded.");
            return;
        }

        Map<Long, Trainee> traineeStorage = (Map<Long, Trainee>) bean;

        trainees.forEach(trainee -> {
            if (trainee.getUserId() != null) {
                traineeStorage.put(trainee.getUserId(), trainee);
            }
        });

        LOGGER.info("Initialized traineeStorage with {} trainee(s).", traineeStorage.size());
    }

    @SuppressWarnings("unchecked")
    private void loadTrainers(Object bean) {
        if (!(bean instanceof Map<?, ?>)) {
            LOGGER.warn("Bean 'trainerStorage' is not a Map. Seed data was not loaded.");
            return;
        }

        Map<Long, Trainer> trainerStorage = (Map<Long, Trainer>) bean;

        trainers.forEach(trainer -> {
            if (trainer.getUserId() != null) {
                trainerStorage.put(trainer.getUserId(), trainer);
            }
        });

        LOGGER.info("Initialized trainerStorage with {} trainer(s).", trainerStorage.size());
    }

    @SuppressWarnings("unchecked")
    private void loadTrainings(Object bean) {
        if (!(bean instanceof Map<?, ?>)) {
            LOGGER.warn("Bean 'trainingStorage' is not a Map. Seed data was not loaded.");
            return;
        }

        Map<Long, Training> trainingStorage = (Map<Long, Training>) bean;

        trainings.forEach(training -> {
            if (training.getId() != null) {
                trainingStorage.put(training.getId(), training);
            }
        });

        LOGGER.info("Initialized trainingStorage with {} training(s).", trainingStorage.size());
    }
}
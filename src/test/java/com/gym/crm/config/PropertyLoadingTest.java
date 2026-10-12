package com.gym.crm.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyLoadingTest {

    @Test
    void shouldLoadStorageInitializationFileProperty() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(StorageConfig.class)) {

            String filePath = context.getEnvironment()
                    .getProperty("storage.init.file");

            assertThat(filePath).isEqualTo("data/init-data.json");
        }
    }
}
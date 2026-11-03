package com.gym.crm.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@TestPropertySource(properties = {
        "spring.profiles.active=local"
})
class EnvironmentProfileTest {

    @Autowired
    private Environment environment;

    @Test
    void shouldActivateLocalProfile() {
        assertThat(environment.getActiveProfiles())
                .contains("local");
    }
}
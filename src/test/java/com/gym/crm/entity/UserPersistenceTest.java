package com.gym.crm.entity;

import com.gym.crm.config.PersistenceConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(PersistenceConfig.class)
@Transactional
class UserPersistenceTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldPersistAndLoadUser() {
        String username = "Test.User." + UUID.randomUUID();

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername(username);
        user.setPassword("FakePass12");
        user.setActive(true);

        entityManager.persist(user);
        entityManager.flush();

        Long id = user.getId();

        entityManager.clear();

        User loadedUser = entityManager.find(User.class, id);

        assertThat(id).isNotNull();
        assertThat(loadedUser).isNotNull();
        assertThat(loadedUser.getUsername()).isEqualTo(username);
        assertThat(loadedUser.getFirstName()).isEqualTo("Test");
        assertThat(loadedUser.getActive()).isTrue();
    }
}
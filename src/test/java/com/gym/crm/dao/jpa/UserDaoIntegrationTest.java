package com.gym.crm.dao.jpa;

import com.gym.crm.config.PersistenceConfig;
import com.gym.crm.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {
        PersistenceConfig.class,
        JpaUserDao.class
})
@Transactional
class UserDaoIntegrationTest {

    @Autowired
    private UserDao userDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldDetectExistingUsername() {
        String username = "Existing.User." + UUID.randomUUID();

        User user = new User();
        user.setFirstName("Existing");
        user.setLastName("User");
        user.setUsername(username);
        user.setPassword("FakePass12");
        user.setActive(true);

        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear();

        assertThat(userDao.existsByUsername(username)).isTrue();
    }

    @Test
    void shouldReturnFalseForUnknownUsername() {
        String username = "Missing.User." + UUID.randomUUID();

        assertThat(userDao.existsByUsername(username)).isFalse();
    }
}
package com.gym.crm.dao.jpa;

import com.gym.crm.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserDao implements UserDao {

    private final EntityManager entityManager;

    public JpaUserDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean existsByUsername(String username) {
        Long count = entityManager.createQuery(
                        """
                        SELECT COUNT(account)
                        FROM User account
                        WHERE account.username = :username
                        """,
                        Long.class
                )
                .setParameter("username", username)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return entityManager.createQuery(
                        """
                        SELECT u
                        FROM User u
                        WHERE u.username = :username
                        """,
                        User.class
                )
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
    }
}
package com.gym.crm.dao.jpa;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

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
}
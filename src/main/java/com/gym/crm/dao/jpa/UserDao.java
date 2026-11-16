package com.gym.crm.dao.jpa;

import com.gym.crm.entity.User;
import java.util.Optional;

public interface UserDao {

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
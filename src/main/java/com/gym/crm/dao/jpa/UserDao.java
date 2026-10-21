package com.gym.crm.dao.jpa;

public interface UserDao {

    boolean existsByUsername(String username);
}
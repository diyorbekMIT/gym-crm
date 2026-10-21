package com.gym.crm.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

class PostgresConnectionTest {

    @Test
    void shouldConnectToGymCrmDatabase() throws Exception {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(
                             PersistenceConfig.class
                     )) {

            DataSource dataSource = context.getBean(DataSource.class);

            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(
                         "SELECT current_database(), current_user"
                 )) {

                assertThat(resultSet.next()).isTrue();

                assertThat(resultSet.getString(1))
                        .isEqualTo("gym_crm");

                assertThat(resultSet.getString(2))
                        .isEqualTo("gym_crm_user");
            }
        }
    }
}
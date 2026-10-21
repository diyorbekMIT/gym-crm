package com.gym.crm.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import jakarta.persistence.EntityManager;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:database.properties")
public class PersistenceConfig {

    private final Environment environment;

    public PersistenceConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public EntityManager entityManager(
            EntityManagerFactory entityManagerFactory
    ) {
        return SharedEntityManagerCreator.createSharedEntityManager(
                entityManagerFactory
        );
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource =
                new DriverManagerDataSource();

        dataSource.setDriverClassName(
                environment.getRequiredProperty("db.driver")
        );

        dataSource.setUrl(
                environment.getRequiredProperty("db.url")
        );

        dataSource.setUsername(
                environment.getRequiredProperty("db.username")
        );

        dataSource.setPassword(
                environment.getRequiredProperty("db.password")
        );

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean factory =
                new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(dataSource);

        factory.setPackagesToScan("com.gym.crm.entity");

        factory.setJpaVendorAdapter(
                new HibernateJpaVendorAdapter()
        );

        Properties properties = new Properties();

        properties.setProperty(
                "hibernate.hbm2ddl.auto",
                environment.getRequiredProperty(
                        "hibernate.hbm2ddl.auto"
                )
        );

        properties.setProperty(
                "hibernate.show_sql",
                environment.getRequiredProperty(
                        "hibernate.show_sql"
                )
        );

        properties.setProperty(
                "hibernate.format_sql",
                environment.getRequiredProperty(
                        "hibernate.format_sql"
                )
        );

        factory.setJpaProperties(properties);

        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer
    databasePropertySourcesPlaceholderConfigurer() {

        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    @DependsOn("entityManagerFactory")
    public InitializingBean trainingTypeInitializer(DataSource dataSource) {
        return () -> {
            ResourceDatabasePopulator populator =
                    new ResourceDatabasePopulator();

            populator.addScript(
                    new ClassPathResource("training-types.sql")
            );

            populator.execute(dataSource);
        };
    }
}
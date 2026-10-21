package com.gym.crm;

import com.gym.crm.config.HibernateAppConfig;
import com.gym.crm.facade.JpaGymFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class HibernateGymCrmApplication {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(HibernateGymCrmApplication.class);

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(
                             HibernateAppConfig.class
                     )) {

            context.getBean(JpaGymFacade.class);

            LOGGER.info(
                    "Hibernate Gym CRM started successfully."
            );
        }
    }
}
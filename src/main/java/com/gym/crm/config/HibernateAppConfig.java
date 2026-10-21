package com.gym.crm.config;

import com.gym.crm.dao.jpa.JpaTraineeDao;
import com.gym.crm.dao.jpa.JpaTrainerDao;
import com.gym.crm.dao.jpa.JpaTrainingDao;
import com.gym.crm.dao.jpa.JpaTrainingTypeDao;
import com.gym.crm.dao.jpa.JpaUserDao;
import com.gym.crm.facade.JpaGymFacade;
import com.gym.crm.service.jpa.AuthenticationService;
import com.gym.crm.service.jpa.ProfileAccountService;
import com.gym.crm.service.jpa.ProfileManagementService;
import com.gym.crm.service.jpa.ProfileRegistrationService;
import com.gym.crm.service.jpa.TrainerAssignmentService;
import com.gym.crm.service.jpa.TraineeDeletionService;
import com.gym.crm.service.jpa.TrainingManagementService;
import com.gym.crm.service.jpa.TrainingQueryService;
import com.gym.crm.util.JpaUsernameGenerator;
import com.gym.crm.util.PasswordGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        PersistenceConfig.class,

        JpaUserDao.class,
        JpaTrainingTypeDao.class,
        JpaTraineeDao.class,
        JpaTrainerDao.class,
        JpaTrainingDao.class,

        JpaUsernameGenerator.class,
        PasswordGenerator.class,

        AuthenticationService.class,
        ProfileRegistrationService.class,
        ProfileAccountService.class,
        ProfileManagementService.class,
        TrainingManagementService.class,
        TrainingQueryService.class,
        TrainerAssignmentService.class,
        TraineeDeletionService.class,

        JpaGymFacade.class
})
public class HibernateAppConfig {
}
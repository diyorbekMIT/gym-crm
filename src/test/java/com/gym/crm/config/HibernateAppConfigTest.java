package com.gym.crm.config;

import com.gym.crm.dao.jpa.TraineeDao;
import com.gym.crm.dao.jpa.TrainerDao;
import com.gym.crm.dao.jpa.TrainingDao;
import com.gym.crm.dao.jpa.TrainingTypeDao;
import com.gym.crm.dao.jpa.UserDao;
import com.gym.crm.facade.JpaGymFacade;
import com.gym.crm.service.jpa.AuthenticationService;
import com.gym.crm.service.jpa.ProfileAccountService;
import com.gym.crm.service.jpa.ProfileManagementService;
import com.gym.crm.service.jpa.ProfileRegistrationService;
import com.gym.crm.service.jpa.TraineeDeletionService;
import com.gym.crm.service.jpa.TrainerAssignmentService;
import com.gym.crm.service.jpa.TrainingManagementService;
import com.gym.crm.service.jpa.TrainingQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = HibernateAppConfig.class)
class HibernateAppConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JpaGymFacade jpaGymFacade;

    @Test
    void shouldLoadHibernateApplicationContext() {
        assertThat(applicationContext).isNotNull();
        assertThat(jpaGymFacade).isNotNull();
    }

    @Test
    void shouldRegisterAllRequiredBeans() {
        assertThat(applicationContext.getBean(UserDao.class)).isNotNull();
        assertThat(applicationContext.getBean(TrainingTypeDao.class)).isNotNull();
        assertThat(applicationContext.getBean(TraineeDao.class)).isNotNull();
        assertThat(applicationContext.getBean(TrainerDao.class)).isNotNull();
        assertThat(applicationContext.getBean(TrainingDao.class)).isNotNull();

        assertThat(applicationContext.getBean(AuthenticationService.class)).isNotNull();
        assertThat(applicationContext.getBean(ProfileRegistrationService.class)).isNotNull();
        assertThat(applicationContext.getBean(ProfileAccountService.class)).isNotNull();
        assertThat(applicationContext.getBean(ProfileManagementService.class)).isNotNull();
        assertThat(applicationContext.getBean(TrainingManagementService.class)).isNotNull();
        assertThat(applicationContext.getBean(TrainingQueryService.class)).isNotNull();
        assertThat(applicationContext.getBean(TrainerAssignmentService.class)).isNotNull();
        assertThat(applicationContext.getBean(TraineeDeletionService.class)).isNotNull();

        assertThat(applicationContext.getBean(JpaGymFacade.class)).isNotNull();
    }
}

package com.gym.crm;

import com.gym.crm.config.HibernateAppConfig;
import com.gym.crm.config.WebConfig;
import org.apache.catalina.LifecycleException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class GymCrmApplication {

    public static void main(String[] args) throws LifecycleException {
        AnnotationConfigWebApplicationContext context =
                new AnnotationConfigWebApplicationContext();
        context.register(HibernateAppConfig.class, WebConfig.class);

        TomcatServer server = new TomcatServer(context);
        server.start();
    }
}
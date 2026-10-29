package com.gym.crm;

import com.gym.crm.logging.RestLoggingFilter;
import jakarta.servlet.DispatcherType;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class TomcatServer {

    private final AnnotationConfigWebApplicationContext context;

    public TomcatServer(AnnotationConfigWebApplicationContext context) {
        this.context = context;
    }

    public void start() throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        String docBase = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        Context tomcatContext = tomcat.addContext("", docBase);

        tomcatContext.setParentClassLoader(Thread.currentThread().getContextClassLoader());

        Tomcat.addServlet(
                tomcatContext,
                "dispatcher",
                new DispatcherServlet(this.context)
        ).setLoadOnStartup(1);

        tomcatContext.addServletMappingDecoded("/", "dispatcher");

        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("restLoggingFilter");
        filterDef.setFilter(new RestLoggingFilter());
        tomcatContext.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("restLoggingFilter");
        filterMap.addURLPattern("/*");
        filterMap.setDispatcher(DispatcherType.REQUEST.name());
        tomcatContext.addFilterMap(filterMap);

        tomcat.start();
        System.out.println("=== Gym CRM started on http://localhost:8080 ===");
        tomcat.getServer().await();
    }
}
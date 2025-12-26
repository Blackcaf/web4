package com.nlshakal.web4.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        packages("com.nlshakal.web4.resource");
        register(com.nlshakal.web4.resource.CachedResultResource.class);
    }
    
    @Bean
    public ServletRegistrationBean<ServletContainer> jerseyServletRegistration() {
        ServletRegistrationBean<ServletContainer> registration = 
            new ServletRegistrationBean<>(new ServletContainer(this), "/api/v1/*");
        registration.setName("jersey-servlet");
        registration.setLoadOnStartup(1);
        return registration;
    }
}


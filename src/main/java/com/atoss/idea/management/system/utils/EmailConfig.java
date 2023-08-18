package com.atoss.idea.management.system.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Configuration
public class EmailConfig {

    /**
     * Configures and provides a FreeMarker template configuration factory bean.
     *
     * This method creates and configures a FreeMarkerConfigurationFactoryBean instance
     * to define the template loader path for FreeMarker templates.
     *
     * @return A FreeMarkerConfigurationFactoryBean configured with the template loader path.
     */
    @Primary
    @Bean
    public FreeMarkerConfigurationFactoryBean factoryBean() {
        FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
        bean.setTemplateLoaderPath("classpath:/templates");
        return bean;
    }
}

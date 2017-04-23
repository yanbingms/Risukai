package com.demo.api;

import com.demo.api.handle.JobHandle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * Created by mhh on 2017/3/17.
 */
@EnableAutoConfiguration
@SpringBootApplication
@PropertySource(value= "classpath:/conf.properties")
public class ApiApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ApiApplication.class, args);

        try {
            ctx.getBean(JobHandle.class).startHandle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean(name = "jobHandle")
    public JobHandle getJobHandle() {
        return new JobHandle();
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedHeaders("*")
                        .allowedMethods("*")
                        .allowedOrigins("*");
            }
        };
    }

}

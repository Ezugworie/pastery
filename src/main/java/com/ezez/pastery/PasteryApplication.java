package com.ezez.pastery;

import com.ezez.pastery.security.JwtAuthenticationFilter;
import com.ezez.pastery.service.RoleService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import java.util.TimeZone;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
//@EnableSwagger2
public class PasteryApplication {
    @Autowired
    RoleService roleService;

    public static void main(String[] args) {
        SpringApplication.run(PasteryApplication.class, args);
    }


    @Bean
    public OpenAPI customOpenAPI(@Value("${application-description}") String applicationDescription,
                                 @Value("${application-version}") String applicationVersion) {

        return new OpenAPI()
                .info(new Info()
                        .title(applicationDescription)
                        .version(applicationVersion)
                        .contact(new Contact().name("Iyke Ezugworie").email("i.ezugworie@gmail.com").url("https://pastery.app"))
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }


    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    //create default roles
    @EventListener
    public void seedRoles(ContextRefreshedEvent event) {
        roleService.initializeRoles();
    }
}

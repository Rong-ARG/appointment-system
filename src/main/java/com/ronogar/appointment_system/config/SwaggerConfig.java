package com.ronogar.appointment_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Appointment System")
                        .version("1.0")
                        .description("Appointment System")
                        .contact(new Contact()
                                .name("Ronogar")
                                .email("abucewiczpablo@gmail.com")
                                .url("https://github.com/Rong-ARG"))
                        .license( new License()
                                .name("MIT")));
    }
}

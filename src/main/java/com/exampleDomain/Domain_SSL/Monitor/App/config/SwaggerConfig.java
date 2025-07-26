package com.exampleDomain.Domain_SSL.Monitor.App.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SSL Domain Monitor API")
                        .version("1.0.0")
                        .description("REST API for monitoring SSL certificate expiry dates")
                        .contact(new Contact()
                                .name("SSL Monitor Team")
                                .email("support@sslmonitor.com")));
    }
}

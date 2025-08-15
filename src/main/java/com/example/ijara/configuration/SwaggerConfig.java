package com.example.ijara.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ijara Uz API")
                        .description("Bu API hujjatlari loyihangiz uchun Swagger orqali avtomatik yaratiladi.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Otabek Nabiyev")
                                .email("onabiyev626@gmail.com")
                                .url("https://your-website.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local server"),
                        new Server().url("https://api.yourdomain.com").description("Production server")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Full Documentation")
                        .url("https://your-docs-link.com"));
    }
}

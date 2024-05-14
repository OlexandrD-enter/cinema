package com.project.mediaservice.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Configuration class for Swagger documentation.
 */
@Configuration
public class SwaggerConfig {
  @Value("${springdoc.swagger-ui.title}")
  private String title;
  @Value("${springdoc.swagger-ui.description}")
  private String description;

  /**
   * Constructs a bean of Swagger Configuration with injected suitable converter for accepting
   * application/octet-stream media type.
   *
   * @param converter MappingJackson2HttpMessageConverter that provides support for
   *                  corresponding media types.
   */
  public SwaggerConfig(MappingJackson2HttpMessageConverter converter) {
    var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
    supportedMediaTypes.add(new MediaType("application", "octet-stream"));
    converter.setSupportedMediaTypes(supportedMediaTypes);
  }

  /**
  * Configures custom OpenAPI settings.
  *
  * @return The custom OpenAPI configuration.
  */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .servers(servers())
        .info(apiInfo())
        .addSecurityItem(new SecurityRequirement()
        .addList("Bearer Authentication"))
        .components(new Components().addSecuritySchemes(
            "Bearer Authentication", createApiKeyScheme()));
  }

  private List<Server> servers() {
    Server server = new Server();
    server.setUrl("/");
    server.setDescription("Default server URL");
    return List.of(server);
  }

  private Info apiInfo() {
    return new Info()
        .title(title)
        .description(description);
  }

  private SecurityScheme createApiKeyScheme() {
    return new SecurityScheme().type(SecurityScheme.Type.HTTP).bearerFormat("JWT")
        .scheme("bearer").description("Enter JWT token");
  }
}

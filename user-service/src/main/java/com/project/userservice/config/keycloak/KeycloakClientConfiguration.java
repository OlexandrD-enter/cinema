package com.project.userservice.config.keycloak;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for setting up Keycloak client properties and creating a Keycloak
 * bean.
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakClientConfiguration.KeycloakProperties.class)
public class KeycloakClientConfiguration {

  private final KeycloakProperties properties;

  /**
   * Configuration properties for Keycloak client.
   */
  @ConfigurationProperties(prefix = "keycloak")
  public record KeycloakProperties(String adminResource,
                                   String adminRealm,
                                   String adminUsername,
                                   String adminPassword,
                                   String authServerUrl) {

  }

  /**
   * Creates and configures a Keycloak bean based on the provided properties.
   *
   * @return Keycloak instance configured with the specified properties.
   */
  @Bean
  public Keycloak keycloak() {
    return KeycloakBuilder.builder()
        .serverUrl(properties.authServerUrl())
        .realm(properties.adminRealm())
        .clientId(properties.adminResource())
        .grantType(OAuth2Constants.PASSWORD)
        .username(properties.adminUsername())
        .password(properties.adminPassword())
        .build();
  }
}

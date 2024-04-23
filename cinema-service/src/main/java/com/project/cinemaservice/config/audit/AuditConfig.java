package com.project.cinemaservice.config.audit;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class for enabling auditing. Auditing is enabled in all profiles besides test
 */
@Configuration
@EnableJpaAuditing
@Profile("!test")
public class AuditConfig {

}

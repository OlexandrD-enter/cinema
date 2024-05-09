package com.project.cinemaservice.config.rabbitmq;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RabbitMQ setup.
 */
@Configuration
@EnableRabbit
public class RabbitConfig {

  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  /**
   * Bean for RabbitTemplate, used for sending messages to RabbitMQ.
   *
   * @param connectionFactory ConnectionFactory to create RabbitTemplate
   * @param messageConverter  MessageConverter to convert messages
   * @return RabbitTemplate instance
   */
  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
      MessageConverter messageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter);
    return rabbitTemplate;
  }
}

# Cinema

## Description

Cinema is a robust platform designed to provide a seamless online ticket shopping to showtime
with such core functionality split by microservices:
- ### **Discovery service**
  - Service location transparency
  - Decoupling services
  - Load balancing
- ### **Gateway service**
  - Single entry point
  - Request routing
  - Service discovery integration
- ### **Authentication service**
  - Log In/out
  - Refresh token
- ### **User service**
  - Register
  - Email confirmation
  - Resend email confirmation
- ### **Media service**
  - Files operations:
    - save
    - get
    - delete
- ### **Payment service**
  - Pay for order
  - Confirm payment
  - Refund payment
- ### **Notification service**
  - Send user email verification msg
  - Send user email receipt about ordered tickets
- ### **Cinema service**
  - ### Admin functionality:
    - Cinema:
      - create
      - update
      - delete
      - get
    - Cinema room:
      - create
      - update
      - delete
      - get
    - Room seat:
      - create
      - update
      - delete
      - get
    - Genre:
      - create
      - update
      - delete
      - get
    - Movie:
      - create
      - update
      - delete
      - get
      - change publish status
      - get all with filters and pagination
    - Order:
      - get all with filters and pagination
  - ### Client functionality:
    - Movie:
      - get
      - get all with filters and pagination
    - Showtime:
      - get
    - Order:
      - create
      - get
      - get all with filters and pagination
      - cancel

## Table of Contents

- [Dependencies](#dependencies)
- [Swagger](#swagger)
- [Quick Start](#docker_compose)

## Dependencies
- [Spring Boot Starter Parent]() - Version 3.2.4
    - Description: Parent project containing the default configuration for building Spring Boot applications, providing dependency management, plugin configurations, and other settings for simplifying the setup and management of Spring Boot projects.
- [Spring Boot Starter Data JPA]()
    - Description: Starter for using Spring Data JPA with Hibernate.
- [Spring Boot Starter Security]()
    - Description: Starter for using Spring Security.
- [Spring Boot Starter Web]()
    - Description: Starter for building web applications with Spring MVC.
- [Spring Boot Starter Webflux]()
    - Description: Starter for building web applications with Spring MVC.
- [Spring Boot Starter AMQP]()
    - Description: Starter for using Spring AMQP to produce and consume messages with RabbitMQ.
- [Spring Cloud Starter Netflix Eureka Server]()
    - Description: Starter for creating a Spring Boot application to act as a Eureka server for service registration and discovery in a microservices architecture.
- [Spring Cloud Starter Netflix Eureka Client]()
    - Description: Starter for integrating a Spring Boot application as a client with Netflix Eureka for service discovery and registration.
- [Spring Cloud Starter OpenFeign]()
    - Description: Starter for integrating OpenFeign, a declarative HTTP client, into Spring Boot applications, simplifying the process of invoking RESTful services via annotated interfaces.
- [Spring Boot Starter OAuth2 Resource Server]() 
    - Description: Starter for securing Spring Boot applications as OAuth2 resource servers, allowing them to validate and process OAuth2 tokens for authorization purposes.
- [Keycloak Admin Client]()
    - Description: Client library for interacting with Keycloak's Admin REST API, facilitating programmatic management of Keycloak realms, users, roles, and other administrative tasks.
- [QueryDSL]() - Version 5.0.0
    - Description: Framework for constructing type-safe SQL-like queries in Java.
- [Flyway Core](https://flywaydb.org/)
    - Description: Starter for building reactive web applications with Spring WebFlux.
- [PostgreSQL](https://jdbc.postgresql.org/) - Version: Runtime
    - Description: PostgreSQL JDBC driver for database connectivity.
- [Lombok](https://projectlombok.org/) - Version 1.18.30 (Optional)
    - Description: Library to reduce boilerplate code in Java classes.
- [MapStruct](https://mapstruct.org/) - Version 1.5.3.Final
    - Description: Code generator for Java bean mappings.
- [Stripe Java]() - Version 24.0.0
    - Description: Java library for interacting with the Stripe API, facilitating the integration of payment processing functionality into Java applications.
- [MockWebServer]() - Version 4.11.0
    - Description: Library for creating a mock HTTP server for testing HTTP clients.
- [OkHttp]() - Version 4.11.0
    - Description: HTTP client for making network requests in Java applications.
- [Springdoc OpenAPI Starter WebMVC UI](https://springdoc.org/) - Version 2.1.0
    - Description: Auto-generates OpenAPI documentation for Spring Boot applications.
- [Spring Boot Starter Test]() 
    - Description: Starter for testing Spring Boot applications.
- [Spring Security Test]()
    - Description: Testing utilities for Spring Security.


## Swagger
The Swagger UI provides a user-friendly interface for exploring and testing the API endpoints. Navigate through the available resources and endpoints to understand the functionalities provided by the API.
### Accessing Swagger UI
To view the Swagger documentation ensure that the project is up and running.

Next, open your web browser and navigate to the following URL:
    ```
    http://[actual_host]:[actual_port]/swagger-ui.html
    ```

Replace [actual_host] with the actual host where your application is running, and [actual_port] with the actual port.
Each service has it owns swagger documentation.

For example:
    ```
    http://localhost:8080/swagger-ui/index.html
    ```
### Authorize
1) Open your Swagger UI in a web browser.
2) Click on "Authorize" Button
3) Enter the value in the following format:
   ```
   [your_token]
   ```
Replace [your_token] with the actual token.
4) Once the token is entered, click the "Authorize" button within the dialog

## Quick Start
<a name="docker_compose"></a>
### Prerequisites
- Docker installed on your system

### Getting Started

1. Clone this repository to your local machine:

    ```bash
    git clone link
    cd cinema
    ```

2. Set environment variables into .env file, example of values shows in [example_of_env_file.txt](example_of_env_file.txt):
  - You can modify environment variables in the `docker-compose.yml` file if needed. Ensure the configurations match your requirements.
- Adjust the variables in the `.env` file to match your specific configurations.
3. Start the all services using Docker Compose prod configuration [docker-compose.yml](docker-compose%2Fprod%2Fdocker-compose.yml), or you can choose local configuration [docker-compose.yml](docker-compose%2Flocal%2Fdocker-compose.yml) to run only external services and run all other services locally:

    ```bash
    docker-compose up -d
    ```

   This command will launch the defined services in detached mode (`-d`), allowing them to run in the background.

### Accessing Services

After running your spring boot app, you can access next services:
 
- **Authentication service:** Access the Authentication service at [http://localhost:8090](http://localhost:8090)
- **Cinema service:** Access the Cinema service at [http://localhost:8092](http://localhost:8092)
- **Discovery service:** Access the Discovery service at [http://localhost:8761](http://localhost:8761)
- **Gateway service:** Access the Gateway service at [http://localhost:8080](http://localhost:8080)
- **Media service:** Access the Media service at [http://localhost:8093](http://localhost:8093)
- **Notification service:** Access the Notification service at [http://localhost:8094](http://localhost:8094)
- **Payment service:** Access the Payment service at [http://localhost:8095](http://localhost:8095)
- **User service:** Access the User service at [http://localhost:8091](http://localhost:8091)
- **Keycloak:** Access the Keycloak admin console at [http://localhost:8082](http://localhost:8082)
- **Postgres:** Access Postgres database on `localhost:5432` (if needed, adjust configurations as per your setup)

### Stopping Services

To stop the services when finished, run:

```bash
docker-compose down
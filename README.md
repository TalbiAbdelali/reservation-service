# Restaurant Reservation System

## Setup Instructions
1. Clone the repository.
2. Navigate to each microservice directory (`reservation-service` and `restaurant-service`) and build the project using Maven.
3. Run the Eureka server, then the API gateway, and finally the microservices.
4. Access H2 console at `/h2-console` to inspect the in-memory database.

## Technical Choices
- **Liquibase**: Chosen for its ease of XML configuration and powerful rollback features.
- **Spring Cloud Contract**: Facilitates contract-driven development, ensuring reliable integration tests.

## Running and Testing
1. Start Eureka Server.
2. Run API Gateway.
3. Run Reservation and Restaurant services.
4. Use Postman or Swagger UI to interact with the APIs.
5. Execute tests using `mvn test`.

## Load Balancer Setup
- Configured in `application.properties` using Spring Cloud LoadBalancer.

## JWT Authentication
- Implemented at API Gateway to secure endpoints.
- Configure JWT secret and expiration in `application.properties`.

## Swagger Documentation
- Access Swagger UI at `/swagger-ui.html` for each microservice.


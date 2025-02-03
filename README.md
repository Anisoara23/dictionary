# WordCraft Dictionary Application

Welcome to WordCraft, an advanced dictionary application tailored for internal use by educational institutions, businesses, or any organization requiring a specialized dictionary.

## Overview
WordCraft offers a comprehensive solution for organizations seeking to enhance internal communication and understanding by providing a platform to manage and explore domain-specific terminology. This customization ensures that employees have access to a tailored lexicon that aligns with the unique vocabulary used within their workplace.

## System Requirements
Before you begin contributing to the WordCraft application, ensure that your development environment meets the following requirements:
1. **Java Development Kit (JDK) 17:** Java 17 is required to build and run the WordCraft application.
2. **Apache Maven:** Maven is used for building and managing dependencies of the WordCraft project.
3. **Docker:** Docker is required for containerization and running the WordCraft database in a containerized environment.
4. **Environment Variables:** Configure environment variables for database connection and server setup.
   - ***PSQL_URL***: URL of the PostgreSQL database.
   - ***PSQL_PORT***: Port number of the PostgreSQL database.
   - ***PSQL_DATABASE***: Name of the PostgreSQL database.
   - ***PSQL_USERNAME***: Username for connecting to the PostgreSQL database.
   - ***PSQL_PASSWORD***: Password for connecting to the PostgreSQL database.
   - ***PGADMIN_PORT***: Port number for accessing pgAdmin or other database management tools.
   - ***SERVER_PORT***: Port number on which the WordCraft server should run.


## Project Setup
Follow these steps to set up the project:
1. Clone the repository: `git clone https://github.com/Anisoara23/dictionary.git`
2. Navigate to the project folder: `cd dictionary/`
3. Start the PostgreSQL database using Docker: `PSQL_PORT=${PSQL_PORT} PSQL_DATABASE=${PSQL_DATABASE} PSQL_USERNAME=${PSQL_USERNAME} PSQL_PASSWORD=${PSQL_PASSWORD} PGADMIN_PORT=${PGADMIN_PORT} docker-compose up -d`
4.  Build the project using Maven: `mvn clean install`

## Profiles
WordCraft has two profiles:
- **view:** Interact with the user interface built with Vaadin.
- **rest:** Interact with RESTful endpoints using tools like Postman.

## User Interface (UI)

To interact with the UI part of the application:
1. Run the application with the "view" profile active: `mvn spring-boot:run -Dspring.profiles.active=view`
2. Open your web browser and navigate to the specified URL: `http://localhost:{SERVER_PORT}/api/v1.0/wordcraft`
3. To login, use one of the emails from the "users" table, password is the same for all users "Pass_1234". Or register a new user and login.

For more information, refer to the WordCraft documentation: https://github.com/Anisoara23/dictionary/tree/main/src/main/resources/documentation

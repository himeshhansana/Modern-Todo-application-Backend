# Todos App - Back End

A small Java EE back-end for a TODOs application. This repository contains a NetBeans/Ant-based web application using Hibernate for persistence. It provides REST-like controllers for user authentication and TODO management.

## Contents
- `src/java/controllers/` - controllers: `LoginController`, `RegisterController`, `TodoController`, `UserController`
- `src/java/models/` - data models: `User`, `Todo`
- `src/java/dtos/` - DTOs used by controllers
- `web/` - web frontend root (contains `index.html` and `WEB-INF`)
- `Todos-App.postman_collection.json` - Postman collection for the API
- `build.xml` - Ant build file (NetBeans/Ant project)
- `src/java/hibernate.cfg.xml` - Hibernate configuration

## Prerequisites
- Java JDK 8 or higher
- Apache Ant (if not using NetBeans)
- GlassFish (or another Java EE servlet container) for deployment
- A relational database for Hibernate (configure in `src/java/hibernate.cfg.xml`)
- (Optional) NetBeans IDE for easy import and deployment

## Quick start
1. Clone or open the project in NetBeans (Import as Existing Project -> choose project folder).
2. Configure your database connection in `src/java/hibernate.cfg.xml` (JDBC URL, username, password, dialect, driver).
3. Build the project:
   - In NetBeans: use the Build action.
   - Or from command line (project root):
     
     ant

4. Deploy the generated WAR to GlassFish. In NetBeans, use the Run/Deploy action. Or copy the generated WAR into GlassFish `domain1/autodeploy`.

## API (controllers overview)
The application exposes controller classes under `src/java/controllers`. Expected functionality (names indicate purpose):

- `RegisterController` — endpoints to register a new user (e.g., `/register`).
- `LoginController` — endpoints to sign in and obtain session/auth response (e.g., `/login`).
- `TodoController` — endpoints to create/read/update/delete TODO items (e.g., `/todos`, `/todos/{id}`).
- `UserController` — endpoints to manage or view user data (e.g., `/users`, `/users/{id}`).

Note: Exact paths and HTTP methods are defined in the controller source code. See the controller files for details and sample request/response formats.

## Postman
A Postman collection is included: `Todos-App.postman_collection.json`. Import it into Postman to see prepared requests for authentication and TODO operations.

## Configuration
- Hibernate: `src/java/hibernate.cfg.xml` — edit DB connection, dialect and mapping settings.
- Web descriptors: `web/WEB-INF/glassfish-web.xml` and `web/WEB-INF` other descriptors may contain deployment settings.

## DTOs and Responses
This project uses DTO classes in `src/java/dtos` for request/response payloads, such as `SignInDTO`, `SignUpDTO`, `CreateTodoDTO`, `SuccessDTO`, and `ErrorDTO`.

## Troubleshooting
- If the app fails to start: check the server log (GlassFish `server.log`) for deployment or classloading errors.
- Database connection errors: verify JDBC URL, driver, credentials, and that the DB is reachable.
- Hibernate mapping errors: ensure entity classes (`src/java/models`) have correct annotations and the `hibernate.cfg.xml` includes mappings if required.

## Tests
There are no automated tests included in this repository. Adding a small integration test or unit tests for controllers and services is recommended.

## Post-setup suggestions
- Add README snippets for each controller with concrete endpoint paths once verified.
- Add unit/integration tests and CI build.

---
Created by project assistant. For help specific to the code, open the controller source files in `src/java/controllers/`.

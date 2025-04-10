# Tool Tracking Application

A containerized full-stack application with Spring Boot backend, React frontend, and MySQL database.

## Project Structure

```
project-root/
├── backend/               # Spring Boot application
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── frontend2/             # React application
│   ├── src/
│   ├── package.json
│   └── Dockerfile
└── docker-compose.yml
```

## Prerequisites

- Docker and Docker Compose
- Java JDK 21 (for local development)
- Node.js v22+ (for local development)

## Setup Instructions

### Backend Setup

1. Create a Spring Boot application with the following dependencies:
   - Spring Web
   - Spring Data JPA
   - MySQL Driver

2. Configure `application.properties`:

```properties
spring.application.name=Backend
spring.datasource.url=jdbc:mysql://database:3306/tooltrack
spring.datasource.username=root
spring.datasource.password=<your password>
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

3. Create a `Dockerfile` in the backend directory:

```dockerfile
FROM maven:3.9-eclipse-temurin-21-alpine as build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
COPY --from=build /app/target/*.jar app.jar
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Frontend Setup

1. Create a React application using Vite:

```bash
npm create vite@latest frontend2 -- --template react
cd frontend2
npm install
```

2. Configure API connection in `App.jsx` to fetch data from the backend:

```jsx
// Use localhost:8080 for development
fetch('http://localhost:8080/hello')
    .then(response => response.json())
    .then(data => setMessage(data.message))
```

3. Create a `Dockerfile` in the frontend directory:

```dockerfile
FROM node:22
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 3000
CMD ["npm", "start"]
```

### Docker Compose Setup

Create a `docker-compose.yml` file in the project root:

```yaml
version: '3.8'

services:
  frontend:
    build: ./frontend2
    container_name: testing_frontend_tooltrack
    ports:
      - "3000:3000"
    depends_on:
      - backend
    environment:
      - REACT_APP_API_URL=http://backend:8080

  backend:
    build: ./backend
    container_name: testing_backend_tooltrack
    ports:
      - "8080:8080"
    depends_on:
      - database
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://database:3306/tooltrack
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=<your password>
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update

  database:
    image: mysql:latest
    container_name: testing_database_tooltrack
    ports:
      - "3307:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=<your password>
      - MYSQL_DATABASE=tooltrack
    volumes:
      - testingtooltrack:/var/lib/mysql

volumes:
  testingtooltrack:
```

## Running the Application

Build and start the containers:

```bash
docker-compose up -d
```

Access the components:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Database: localhost:3307 (external access)

## FAQ

### How to shutdown gracefully the docker images

**Solution**: 
```bash
docker-compose down
```

### Port Conflicts with Local MySQL

**Issue**: Error when starting containers due to port conflicts with locally installed MySQL.

**Solution**: Change the port mapping in docker-compose.yml from `3306:3306` to `3307:3306`.

### Database Connection Issues

**Issue**: Backend can't connect to the database.

**Solution**: Ensure your Spring Boot configuration uses the container name `database` as the host:

```properties
spring.datasource.url=jdbc:mysql://database:3306/tooltrack
```

### Data Persistence

**Issue**: Data disappears after container restart.

**Solution**: The named volume `testingtooltrack` persists data between container restarts. If data loss occurs, verify volume configuration.

### Frontend API Connection

**Issue**: Frontend fails to fetch data from backend.

**Solution**: For development, ensure API calls use `localhost:8080`. For production within Docker, use container names.

### Backend Taking Too Long to Start

**Issue**: Backend fails because database isn't ready.

**Solution**: Add `depends_on` in docker-compose.yml and implement connection retry logic in your Spring Boot application.

### Volume Security

**Issue**: Concerns about volume security.

**Solution**: Docker volumes are properly isolated. For additional security:
1. Use specific user mappings
2. Set appropriate file permissions
3. Consider encryption for sensitive data

### Cannot Access Docker Container

**Issue**: Unable to access services after starting containers.

**Solution**: Check container status with `docker-compose ps` and look for errors with `docker-compose logs`.

### When I attempt to view the Database in Docker, i can't see its contents (data inserted from the frontend)

**Issue**: Unable to view the docker database in IntelliJ Database Viewer.

**Solution**: 

  ![image](https://github.com/user-attachments/assets/10195a7c-5c6b-496f-b5cd-59cd8c62bc2a)


based on this **Youtube|The Coding Slot** video: https://www.youtube.com/watch?v=DQdB7wFEygo

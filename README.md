# Safora - Context-Aware Decision Navigation

Safora is a premium, context-aware navigation application built to prioritize safety, real-time community reports, and dynamic route adjustments. Moving beyond standard shortest-path routing, Safora dynamically evaluates route safety based on live conditions like lighting, police checkpoints, community hazard reports, and traffic flow.

## 🚀 Key Features

* **Context-Aware Routing:** Calculates routes considering multiple live contexts (e.g., Night Safety, Construction, Protests, Floods).
* **Live Community Reports:** Users can report incidents (harassment, poor lighting, accidents) that immediately factor into future route safety scores.
* **Dynamic Journey Progress:** Live updates with context chips detailing why a route is currently recommended or why a reroute occurred.
* **Premium UI/UX:** Built with a modern, responsive, and minimalist aesthetic inspired by premium applications.

## 🏗️ Architecture

The project is split into a robust backend API and a desktop client application.

### Backend (`safora-backend`)
* **Framework:** Spring Boot (Java 17)
* **Database:** PostgreSQL (with PostGIS for geospatial queries, managed via Docker)
* **Key Integrations:** 
  * Spring Security & JWT for Authentication
  * OpenRouteService / GraphHopper for route mapping algorithms
  * Hibernate/JPA for data persistence

### Frontend (`safora-frontend`)
* **Framework:** JavaFX
* **Map Engine:** Leaflet.js embedded via JavaFX WebView
* **Build Tool:** Maven

## 🛠️ Setup & Installation

### Prerequisites
* Java 17+
* Maven (`mvn`)
* Docker (for PostgreSQL database)

### 1. Database Setup
The backend relies on a PostgreSQL database with PostGIS enabled. 
1. Navigate to the `safora-backend` directory.
2. Start the database using Docker Compose:
```bash
docker-compose up -d
```

### 2. Running the Backend
1. Open a terminal and navigate to the backend directory:
```bash
cd safora-backend
```
2. Start the Spring Boot application:
```bash
./mvnw spring-boot:run
```
*(Wait until you see "Started SaforaApplication" in the terminal before starting the frontend).*

### 3. Running the Frontend
The frontend can be run directly from an IDE or via Maven. 

**Option A: Running from an IDE (Recommended)**
1. Open the project in your preferred IDE (e.g., VS Code, IntelliJ).
2. Navigate to `safora-frontend/src/main/java/com/safora/client/MainApp.java`.
3. Click **Run** on the `public static void main(String[] args)` method.

**Option B: Running from the Terminal**
1. Open a new terminal and navigate to the frontend directory:
```bash
cd safora-frontend
```
2. Run the JavaFX application:
```bash
mvn clean javafx:run
```
*(Note: If you encounter a `jdk.jsobject not found` error on macOS, please use Option A).*

## 📖 Usage Flow (Demo)

1. **Login:** Log in with the pre-configured demo account or create a new user.
2. **Search:** Enter a destination in the Home screen.
3. **Analyze Journey:** Safora will scan multiple routes, displaying the safety trade-offs (Recommended, Balanced, Fastest).
4. **Live Navigation:** Begin the journey. The map will update progressively, displaying dynamic UI checkpoints (e.g., passing a Police Checkpoint or hitting a well-lit zone).

## 🛡️ License

This project was developed as a major academic project. All rights reserved.

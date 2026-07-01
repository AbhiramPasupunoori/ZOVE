# ZOVE

ZOVE is a social media application with a Spring Boot backend and a Vite React frontend.

## Project Structure


ZOVE
  zove-backend     Spring Boot API, auth, database, uploads, WebSocket support
  zove-frontend    React + Vite frontend


## Required Software

Install these before running the project:

- **Git**: to clone and manage the project.
- **Java JDK 21**: required by the backend `pom.xml`.
- **MySQL Server 8+**: used by the backend database.
- **Node.js 20 LTS or newer**: required for the frontend.
- **npm**: installed automatically with Node.js.
- **Maven**: optional, because the backend includes Maven Wrapper scripts: `./mvnw` and `mvnw.cmd`.

Optional but useful:

- **MySQL Workbench** or another database GUI.
- **VS Code** or IntelliJ IDEA.
- Java/Lombok IDE support if your editor shows Lombok warnings.

On macOS with Homebrew, you can install the main tools with:


brew install git openjdk@21 node mysql
brew services start mysql


## Backend Setup

The backend runs on:


http://localhost:8080


The health check endpoint is:


http://localhost:8080/api/health


### 1. Start MySQL

Make sure MySQL is running.

macOS with Homebrew:


brew services start mysql

Or start MySQL from MySQL Workbench/System Settings if you installed it manually.

### 2. Create the Database

Open MySQL:


mysql -u root -p


Then run:

SQl;

CREATE DATABASE IF NOT EXISTS zove_db;


Exit MySQL:


exit;


### 3. Set Backend Environment Variables

The backend reads database and JWT settings from environment variables. Use your own MySQL password here:

macOS/Linux:

export ZOVE_DB_URL="jdbc:mysql://localhost:3306/zove_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export ZOVE_DB_USERNAME="root"
export ZOVE_DB_PASSWORD="your_mysql_password"
export ZOVE_JWT_SECRET="change-this-to-a-long-secret-at-least-32-characters"

Windows PowerShell:

$env:ZOVE_DB_URL="jdbc:mysql://localhost:3306/zove_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:ZOVE_DB_USERNAME="root"
$env:ZOVE_DB_PASSWORD="your_mysql_password"
$env:ZOVE_JWT_SECRET="change-this-to-a-long-secret-at-least-32-characters"


### 4. Run the Backend

From the project root:


cd zove-backend
./mvnw spring-boot:run


On Windows:


cd zove-backend
.\mvnw.cmd spring-boot:run


If macOS/Linux says the wrapper is not executable:


chmod +x mvnw
./mvnw spring-boot:run


### 5. Test the Backend

In a new terminal:


curl http://localhost:8080/api/health


You should see a response with:


status: UP
message: ZOVE backend is working


## Frontend Setup

The frontend runs on:


http://localhost:5173


The frontend uses this API by default:


http://localhost:8080/api
`

### 1. Install Frontend Dependencies

From the project root:


cd zove-frontend
npm install


### 2. Optional Frontend Environment File

The frontend already defaults to `http://localhost:8080/api`. If you want to set it explicitly, create `zove-frontend/.env.local`:


VITE_API_BASE_URL=http://localhost:8080/api


### 3. Run the Frontend

npm run dev

Open:


http://localhost:5173


## Start the Full App

Use two terminals.

Terminal 1, backend:


cd zove-backend
./mvnw spring-boot:run


Terminal 2, frontend:


cd zove-frontend
npm install
npm run dev


Then open:

http://localhost:5173


## Useful Commands

Backend tests:

cd zove-backend
./mvnw test


Frontend lint:


cd zove-frontend
npm run lint


Frontend production build:


cd zove-frontend
npm run build


Preview frontend production build:


cd zove-frontend
npm run preview


## Troubleshooting

If the backend cannot connect to MySQL:

- Make sure MySQL is running.
- Make sure `zove_db` exists.
- Check `ZOVE_DB_USERNAME` and `ZOVE_DB_PASSWORD`.

If port `8080` is already in use:

- Stop the other backend process, or change `server.port` in `zove-backend/src/main/resources/application.properties`.

If port `5173` is already in use:

- Vite may choose another port such as `5174`.
- The backend CORS config already allows `5173` and `5174` for local development.

If `npm` is not found:

- Install Node.js, then restart your terminal.

If `java` is not found:

- Install JDK 21, then restart your terminal.

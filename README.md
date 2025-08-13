# AI Test Generator

AI-powered tool for automatically generating JUnit tests from existing Java code. Supports developers, QA teams, and TDD workflows by reducing test writing time and increasing coverage.

## Features

- 🤖 Automatic JUnit 5 test generation using OpenAI
- 📊 Automatic Java code analysis with JavaParser
- 🎯 Support for different test types (Unit, Integration, Mock)
- 📈 Estimated coverage calculation
- 💡 Suggestions for test improvements
- 🔧 REST API for integration with other tools

## Prerequisites

### For Docker (Recommended)
- Docker & Docker Compose
- OpenAI API Key

### For Manual Setup
- Java 21+
- Maven 3.8+
- Node.js 18+
- OpenAI API Key

## Setup

### 🐳 Docker Setup (Recommended)

1. Clone the repository
2. Configure your OpenAI API key:
   ```bash
   # Windows
   set OPENAI_API_KEY=your-api-key-here
   start-docker.bat
   
   # Linux/Mac
   export OPENAI_API_KEY=your-api-key-here
   chmod +x start-docker.sh
   ./start-docker.sh
   ```

3. Access the application at `http://localhost:8080`

### 📦 Manual Setup

1. Clone the repository
2. Configure your OpenAI API key:
   ```bash
   export OPENAI_API_KEY=your-api-key-here
   ```
   Or modify `src/main/resources/application.properties`

3. Install MCP server dependencies:
   ```bash
   cd mcp-server && npm install
   ```

4. Start MCP server:
   ```bash
   cd mcp-server && npm start
   ```

5. Start Spring Boot (in another terminal):
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

### Generate Tests
```http
POST /api/v1/test-generator/generate
Content-Type: application/json

{
  "sourceCode": "public class Calculator { public int add(int a, int b) { return a + b; } }",
  "className": "Calculator",
  "packageName": "com.example",
  "testType": "UNIT_TEST",
  "coverageLevel": "COMPREHENSIVE"
}
```

### Generate and Export Tests (NEW!)
```http
POST /api/v1/test-generator/generate-and-export
Content-Type: application/json

{
  "sourceCode": "public class Calculator { public int add(int a, int b) { return a + b; } }",
  "className": "Calculator",
  "packageName": "com.example",
  "testType": "UNIT_TEST",
  "coverageLevel": "COMPREHENSIVE",
  "projectPath": "/path/to/your/java/project"
}
```

### Analyze Code
```http
POST /api/v1/test-generator/analyze
Content-Type: application/json

{
  "sourceCode": "public class Calculator { public int add(int a, int b) { return a + b; } }"
}
```

### Health Check
```http
GET /api/v1/test-generator/health
```

### Templates and Configurations
```http
GET /api/v1/test-generator/templates
```

## Web Interface

After starting the application, visit `http://localhost:8080` to use the web interface which includes:

- **Generate Tests**: Interface for generating tests without saving them
- **Generate and Export**: Generate tests and automatically save them to `/src/test/java/...`
- **Analyze Code**: Analyze Java code structure

## Supported Test Types

- **UNIT_TEST**: Isolated unit tests
- **INTEGRATION_TEST**: Integration tests
- **MOCK_TEST**: Tests with mocks and stubs

## Coverage Levels

- **BASIC**: Basic tests for main functionality
- **COMPREHENSIVE**: Complete tests with edge cases
- **EDGE_CASES**: Focus on edge cases and errors

## Usage Examples

### Testing a simple class
```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    public int divide(int a, int b) {
        if (b == 0) throw new IllegalArgumentException("Cannot divide by zero");
        return a / b;
    }
}
```

The AI will generate tests for:
- Addition with positive/negative numbers
- Normal division
- Division by zero handling
- Edge cases (overflow, etc.)

## Testing

Run tests:
```bash
mvn test
```

## Technologies Used

- Spring Boot 3.2.5
- Spring AI with OpenAI
- JavaParser 3.25.8
- JUnit 5
- Mockito
- AssertJ
- Docker & Docker Compose
- Node.js MCP Server

## Docker Commands

```bash
# Start services
docker-compose up --build

# Start in background
docker-compose up -d --build

# Stop services
docker-compose down

# View logs
docker-compose logs -f

# Rebuild and restart
docker-compose up --build --force-recreate
```

@echo off
echo Starting AI Test Generator with Docker...

REM Check if Docker is running
docker version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

REM Check if OPENAI_API_KEY is set
if "%OPENAI_API_KEY%"=="" (
    echo Error: OPENAI_API_KEY environment variable is not set.
    echo Please set your OpenAI API key:
    echo   set OPENAI_API_KEY=your-api-key-here
    pause
    exit /b 1
)

echo Building and starting containers...
docker-compose up --build

echo.
echo AI Test Generator stopped.
pause
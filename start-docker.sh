#!/bin/bash

echo "Starting AI Test Generator with Docker..."

# Check if Docker is running
if ! docker version >/dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Check if OPENAI_API_KEY is set
if [ -z "$OPENAI_API_KEY" ]; then
    echo "Error: OPENAI_API_KEY environment variable is not set."
    echo "Please set your OpenAI API key:"
    echo "  export OPENAI_API_KEY=your-api-key-here"
    exit 1
fi

echo "Building and starting containers..."
docker-compose up --build

echo ""
echo "AI Test Generator stopped."
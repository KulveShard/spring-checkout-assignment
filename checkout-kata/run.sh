#!/bin/bash

# Checkout Kata CLI Launcher
# Usage: ./run.sh

set -e

echo "🚀 Starting Checkout Kata CLI..."

# Start Docker Compose in CLI mode
echo "📦 Starting containers in CLI mode..."
docker-compose -f docker-compose-cli.yml up -d --build

# Wait for kata to be ready
echo "⏳ Waiting for kata to start..."
sleep 8

# Check if container is running
if ! docker ps --format "table {{.Names}}" | grep -q "checkout-kata"; then
    echo "❌ Error: checkout-kata container failed to start"
    docker-compose -f docker-compose-cli.yml logs checkout-kata
    exit 1
fi

echo "✅ Kata ready!"
echo "🖥️  Opening CLI interface..."
echo "🖥️ Connecting directly..."
echo "Press 0 + Enter to see welcome info"
echo "💡 To exit safely: Ctrl+P, then Ctrl+Q"
docker attach checkout-kata
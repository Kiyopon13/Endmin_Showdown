#!/bin/bash
# This script starts a local web server to serve the Endmin Showdown web UI

# Check if Python is available
if command -v python3 &> /dev/null; then
    echo "Starting server with Python 3..."
    echo "Open browser to: http://localhost:8000"
    python3 -m http.server 8000
elif command -v python &> /dev/null; then
    echo "Starting server with Python 2..."
    echo "Open browser to: http://localhost:8000"
    python -m SimpleHTTPServer 8000
elif command -v node &> /dev/null; then
    echo "Node.js detected. Installing http-server if needed..."
    npx http-server -p 8000
else
    echo "Error: No suitable server found."
    echo "Please install Python or Node.js, or serve files manually."
    exit 1
fi

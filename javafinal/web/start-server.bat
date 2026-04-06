@echo off
REM Windows batch script to start a local web server
REM This script serves the Endmin Showdown web UI

echo.
echo ╔═══════════════════════════════════════════════════════════╗
echo ║          ENDMIN SHOWDOWN - LOCAL SERVER STARTER           ║
echo ╚═══════════════════════════════════════════════════════════╝
echo.

REM Check if Python 3 is available
where python >nul 2>nul
if %ERRORLEVEL% == 0 (
    echo Starting Python HTTP Server on http://localhost:8000
    echo.
    echo Press Ctrl+C to stop the server.
    echo.
    python -m http.server 8000
    goto end
)

REM Check if Python is available in Path
where python3 >nul 2>nul
if %ERRORLEVEL% == 0 (
    echo Starting Python 3 HTTP Server on http://localhost:8000
    echo.
    echo Press Ctrl+C to stop the server.
    echo.
    python3 -m http.server 8000
    goto end
)

REM Check if Node.js is available
where node >nul 2>nul
if %ERRORLEVEL% == 0 (
    echo Starting Node.js HTTP Server on http://localhost:8000
    echo.
    echo Press Ctrl+C to stop the server.
    echo.
    npx http-server -p 8000
    goto end
)

REM If no server found
echo.
echo ERROR: No suitable server found!
echo.
echo Please install one of:
echo   - Python (https://www.python.org)
echo   - Node.js (https://nodejs.org)
echo.
echo Alternatively, you can:
echo   1. Open index.html directly in your browser
echo   2. Use your IDE's built-in server
echo   3. Use a local server like XAMPP or Live Server
echo.

:end
pause

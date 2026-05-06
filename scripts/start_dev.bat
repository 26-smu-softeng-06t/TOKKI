@echo off
if exist "C:\Program Files\Java\jdk-21.0.11\bin\java.exe" (
  set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.11"
  set "PATH=%JAVA_HOME%\bin;%PATH%"
)
echo Starting TOKKI Backend (port 8080)...
start "TOKKI Backend" cmd /k "cd /d d:\Soft_Eng\TOKKI && gradlew.bat :backend:bootRun"
timeout /t 5 /nobreak >nul
echo Starting TOKKI Frontend (port 5173)...
start "TOKKI Frontend" cmd /k "cd /d d:\Soft_Eng\TOKKI\frontend && npm run dev"
echo Both servers are starting. Check the new windows.

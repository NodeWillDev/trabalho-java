@echo off
cd /d "%~dp0"
where java >nul 2>nul
if errorlevel 1 (
  echo Instale o Java 17 ou superior para abrir o programa.
  pause
  exit /b 1
)
if not exist "Biblioteca.jar" (
  echo O arquivo Biblioteca.jar nao foi encontrado. Extraia todo o ZIP na mesma pasta.
  pause
  exit /b 1
)
java -jar Biblioteca.jar
if errorlevel 1 pause

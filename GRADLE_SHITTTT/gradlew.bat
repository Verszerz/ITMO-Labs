@echo off
chcp 65001 > nul
setlocal

set "PROJECT_DIR=%~dp0"
set "SRC_DIR=%PROJECT_DIR%src\main\java"
set "OUT_DIR=%PROJECT_DIR%build\classes\main"
set "JAR_DIR=%PROJECT_DIR%build\libs"
set "JAR_NAME=lab5.jar"
set "SOURCES=%PROJECT_DIR%sources.txt"

if "%1"=="run"   goto run
if "%1"=="build" goto build
if "%1"=="clean" goto clean
echo Доступные команды: build, run, clean
goto end

:clean
echo [Gradle] Очистка...
if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
if exist "%JAR_DIR%\%JAR_NAME%" del "%JAR_DIR%\%JAR_NAME%"
echo [Gradle] Очистка завершена.
goto end

:build
echo [Gradle] Компиляция...
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"
if not exist "%JAR_DIR%"  mkdir "%JAR_DIR%"
dir /s /b "%SRC_DIR%\*.java" > "%SOURCES%"
java -m jdk.compiler/com.sun.tools.javac.Main -encoding UTF-8 -d "%OUT_DIR%" "@%SOURCES%"
if errorlevel 1 (
    echo [Gradle] ОШИБКА компиляции!
    del "%SOURCES%" 2>nul
    goto end
)
del "%SOURCES%" 2>nul
echo [Gradle] Компиляция успешна.
python "%PROJECT_DIR%make_jar.py" "%OUT_DIR%" "%JAR_DIR%\%JAR_NAME%"
if errorlevel 1 (
    echo [Gradle] ОШИБКА создания JAR!
    goto end
)
echo [Gradle] Сборка завершена успешно.
goto end

:run
call :build
if not exist "%JAR_DIR%\%JAR_NAME%" goto end
echo [Gradle] Запуск программы...
if "%COLLECTION_FILE%"=="" set COLLECTION_FILE=collection.xml
java -jar "%JAR_DIR%\%JAR_NAME%"
goto end

:end
endlocal

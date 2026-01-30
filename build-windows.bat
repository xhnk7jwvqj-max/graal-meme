@echo off
REM Build script for Windows native executable using GraalVM
REM Requirements: GraalVM JDK 21+ with native-image installed, Visual Studio Build Tools

echo ============================================
echo Double Pendulum - Native Image Build Script
echo ============================================

REM Check if GRAALVM_HOME is set
if "%GRAALVM_HOME%"=="" (
    echo ERROR: GRAALVM_HOME environment variable is not set
    echo Please install GraalVM and set GRAALVM_HOME
    echo Download from: https://www.graalvm.org/downloads/
    exit /b 1
)

REM Check if native-image is available
"%GRAALVM_HOME%\bin\native-image" --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: native-image not found
    echo Installing native-image component...
    "%GRAALVM_HOME%\bin\gu" install native-image
)

REM Set up Visual Studio environment (adjust path as needed)
if exist "C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat" (
    call "C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat"
) else if exist "C:\Program Files\Microsoft Visual Studio\2022\BuildTools\VC\Auxiliary\Build\vcvars64.bat" (
    call "C:\Program Files\Microsoft Visual Studio\2022\BuildTools\VC\Auxiliary\Build\vcvars64.bat"
) else if exist "C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build\vcvars64.bat" (
    call "C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build\vcvars64.bat"
) else (
    echo WARNING: Visual Studio environment not found
    echo Native image build may fail without Visual Studio Build Tools
)

echo.
echo Building with Maven...
call mvn clean package -Pnative -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================================
    echo BUILD SUCCESSFUL!
    echo ============================================
    echo.
    echo Executable location: target\double-pendulum.exe
    echo.
    for %%F in (target\double-pendulum.exe) do echo File size: %%~zF bytes
) else (
    echo.
    echo BUILD FAILED!
    exit /b 1
)

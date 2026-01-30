#!/bin/bash
# Build script for Linux native executable using GraalVM

echo "============================================"
echo "Double Pendulum - Native Image Build Script"
echo "============================================"

# Check if GRAALVM_HOME is set
if [ -z "$GRAALVM_HOME" ]; then
    echo "ERROR: GRAALVM_HOME environment variable is not set"
    echo "Please install GraalVM and set GRAALVM_HOME"
    echo "Download from: https://www.graalvm.org/downloads/"
    exit 1
fi

# Check if native-image is available
if ! "$GRAALVM_HOME/bin/native-image" --version &> /dev/null; then
    echo "ERROR: native-image not found"
    echo "Installing native-image component..."
    "$GRAALVM_HOME/bin/gu" install native-image
fi

# Check for required packages on Linux
if command -v apt-get &> /dev/null; then
    echo "Checking for required dependencies..."
    # These are needed for AWT/Swing on Linux
    dpkg -l | grep -q libfreetype6-dev || echo "WARNING: libfreetype6-dev may be needed"
    dpkg -l | grep -q libfontconfig1-dev || echo "WARNING: libfontconfig1-dev may be needed"
fi

echo ""
echo "Building with Maven..."
mvn clean package -Pnative -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "============================================"
    echo "BUILD SUCCESSFUL!"
    echo "============================================"
    echo ""
    echo "Executable location: target/double-pendulum"
    echo ""
    if [ -f target/double-pendulum ]; then
        SIZE=$(stat --printf="%s" target/double-pendulum 2>/dev/null || stat -f%z target/double-pendulum 2>/dev/null)
        SIZE_MB=$(echo "scale=2; $SIZE / 1048576" | bc)
        echo "File size: $SIZE bytes (~${SIZE_MB} MB)"
    fi
else
    echo ""
    echo "BUILD FAILED!"
    exit 1
fi

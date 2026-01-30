# Double Pendulum Native Executable

A chaotic double pendulum physics simulation built with Java Swing, compiled to a standalone native executable using GraalVM Native Image.

![Double Pendulum](https://upload.wikimedia.org/wikipedia/commons/4/45/Double-compound-pendulum.gif)

## Features

- **Real-time physics simulation** using Lagrangian mechanics
- **Interactive controls**: Sliders to adjust pendulum arm lengths
- **Play/Pause/Reset** controls
- **Trail visualization** showing chaotic motion paths
- **Zero dependencies**: Runs without Java VM installed

## Quick Start (Pre-built)

Simply download `double-pendulum.exe` (Windows) or `double-pendulum` (Linux) from the releases and run it. No Java installation required!

## Building from Source

### Prerequisites

1. **GraalVM JDK 21+** with Native Image support
   - Download: https://www.graalvm.org/downloads/
   - Set `GRAALVM_HOME` environment variable

2. **For Windows**: Visual Studio Build Tools 2019/2022
   - Install "Desktop development with C++" workload

3. **For Linux**: Build essentials and graphics libraries
   ```bash
   sudo apt-get install build-essential libfreetype6-dev libfontconfig1-dev
   ```

4. **Maven 3.8+**

### Install Native Image Component

```bash
# Using GraalVM Updater
$GRAALVM_HOME/bin/gu install native-image
```

### Build Commands

**Windows:**
```batch
build-windows.bat
```

**Linux/macOS:**
```bash
chmod +x build-linux.sh
./build-linux.sh
```

**Or using Maven directly:**
```bash
mvn clean package -Pnative
```

The executable will be created at `target/double-pendulum.exe` (Windows) or `target/double-pendulum` (Linux).

## Expected Binary Size

The native executable size depends on several factors:

| Platform | Typical Size | Notes |
|----------|--------------|-------|
| Windows  | 40-60 MB     | Includes full AWT/Swing |
| Linux    | 35-55 MB     | Slightly smaller |

**Size breakdown:**
- Core JDK runtime: ~20-25 MB
- AWT/Swing GUI framework: ~15-20 MB
- Java2D rendering: ~5-10 MB
- Application code: <1 MB

### Why is it this size?

GraalVM native-image includes:
- The complete AWT/Swing graphics stack
- Java2D rendering engine
- Font rendering (FreeType)
- All necessary platform bindings

This is significantly smaller than bundling a full JRE (~200+ MB) while providing instant startup.

## Running the Application

### As JAR (requires Java)
```bash
java -jar target/double-pendulum-1.0.0.jar
```

### As Native Executable
```bash
# Windows
double-pendulum.exe

# Linux
./double-pendulum
```

## Controls

| Control | Function |
|---------|----------|
| Length 1 Slider | Adjust upper pendulum arm length (50-250 px) |
| Length 2 Slider | Adjust lower pendulum arm length (50-250 px) |
| Play Button | Start/pause simulation |
| Reset Button | Reset to initial position |

## Physics

The simulation uses the equations of motion for a double pendulum derived from Lagrangian mechanics:

```
θ₁'' = f(θ₁, θ₂, θ₁', θ₂', m₁, m₂, l₁, l₂, g)
θ₂'' = g(θ₁, θ₂, θ₁', θ₂', m₁, m₂, l₁, l₂, g)
```

The double pendulum is a classic example of a chaotic system - small changes in initial conditions lead to dramatically different trajectories.

## Troubleshooting

### Build fails on Windows
- Ensure Visual Studio Build Tools are installed
- Run from "x64 Native Tools Command Prompt"
- Check that `GRAALVM_HOME` points to GraalVM JDK (not regular JDK)

### Missing fonts on Linux
```bash
sudo apt-get install fonts-dejavu
```

### Application doesn't start
- Check for missing native libraries: `ldd double-pendulum`
- Ensure display is available: `echo $DISPLAY`

## Project Structure

```
├── pom.xml                                    # Maven build with native-image plugin
├── build-windows.bat                          # Windows build script
├── build-linux.sh                             # Linux build script
├── src/main/java/com/pendulum/
│   └── DoublePendulum.java                    # Main application
└── src/main/resources/META-INF/native-image/
    ├── reflect-config.json                    # Reflection configuration
    ├── jni-config.json                        # JNI configuration
    ├── resource-config.json                   # Resource bundles
    └── native-image.properties                # Build properties
```

## License

MIT License - Feel free to use and modify.

## Research Notes

This project demonstrates:
1. Feasibility of compiling Swing applications to native executables
2. Binary size implications of including the full GUI stack
3. Trade-offs between startup time and executable size

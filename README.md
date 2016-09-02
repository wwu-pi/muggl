# Muggl

## Some notes for the developer

**General remark**: The build system of Muggl was recently switched to Gradle, which is now managing dependencies as well as the actual build. If you experience any problems with that, please let the developers know!

In order to **run** the system:
- Install a JDK (tested exhaustively with OpenJDK 6, but OpenJDK 8 seems to work as well)
- In the root directory that you cloned, enter `./gradlew run`. Muggl is then compiled and started.

In order to **generate an eclipse project** for development and execution:
- In the root directory that you cloned, enter `./gradlew eclipse`. Metadata for all project is generated in accordance with the settings of the build script.
- Check that the `conf` folder is in the `muggl-swt` directory.

In order to create a **deployable jar** file:
- (todo)

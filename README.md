# Muggl

## Some notes for the developer

**General remark**: The build system of Muggl was recently switched to Gradle, which is now managing dependencies as well as the actual build. If you experience any problems with that, please let the developers know!

In order to **run** the system:
- Install Gradle (2.4+ tested).
- Edit the file `gradle.properties` to reflect the path to an installation of OpenJDK 6.
- In the root directory that you cloned, enter `gradle run`. Muggl is then compiled and started.
- tested on jdk6; 7 and 8 currently not supported

In order to **generate an eclipse project** for development and execution:
- In the root directory that you cloned, enter `gradle eclipse`. Metadata for all project is generated in accordance with the settings of the build script.
- Check that the `conf` folder is in the `muggl-swt` directory.

In order to create a **deployable jar** file:
- (todo)

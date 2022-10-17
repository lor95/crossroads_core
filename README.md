## Container:

Commands:
* Build image: `docker build -t crossroads .`
* Run image: `docker run -it -n crossroads --rm -v /tmp/.X11-unix:/tmp/.X11-unix -v "$(pwd)"/data:/crossroads/data -e DISPLAY=$DISPLAY --device /dev/snd crossroads` (`xhost +local:`)

## Local build instructions:

* add `/src/CrossRoads/Libraries/derby.jar` and `/src/CrossRoads/Libraries/derbytools.jar` to Java build path;
* download JavaFX SDK (https://openjfx.io/) and add `javafx-sdk-<VERSION>/lib/javafx.base.jar` and `javafx-sdk-<VERSION>/lib/javafx.graphics.jar` to Java build path.
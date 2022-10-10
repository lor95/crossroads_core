## Container:

`docker build -t CrossRoads .`
`docker run -it --rm -v /tmp/.X11-unix:/tmp/.X11-unix  -e DISPLAY=$DISPLAY CrossRoads`

## building instructions:

- add `/src/CrossRoads/Libraries/derby.jar` and `/src/CrossRoads/Libraries/derbytools.jar` to Java build path;
- download JavaFX SDK (https://openjfx.io/) and add 
`javafx-sdk-<VERSION>/lib/javafx.base.jar` and `javafx-sdk-<VERSION>/lib/javafx.graphics.jar` to Java build path.
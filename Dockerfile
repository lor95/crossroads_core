FROM ubuntu:18.04

ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update && apt-get install -y --no-install-recommends openjdk-11-jdk xorg libgl1-mesa-glx openjfx 
RUN rm -rf /var/lib/apt/lists/*

ENV TZ "Europe/Rome"
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir /crossroads /crossroads/data
COPY . /crossroads
WORKDIR /crossroads

ENV CLASSPATH './src/CrossRoads/Libraries/derby.jar:./src/CrossRoads/Libraries/derbytools.jar:/usr/share/openjfx/lib/javafx.base.jar:/usr/share/openjfx/lib/javafx.graphics.jar:./src/'

RUN javac -encoding UTF-8 ./src/CrossRoads/*.java ./src/CrossRoads/GUI/*.java \
          ./src/CrossRoads/GameSession/*.java ./src/CrossRoads/GameSession/Elements/*.java \
          ./src/CrossRoads/GameSession/Entities/*.java ./src/CrossRoads/GameSession/Entities/Vehicles/*.java

CMD ["java", "-Dderby.system.home=/crossroads/data", "CrossRoads.Game"]
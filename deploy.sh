#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-1.17.0-openjdk-amd64

echo -e "--------------- STOPPING ALL DOCKER CONTAINERS ---------------\n\n"
# shellcheck disable=SC2046
sudo docker stop $(sudo docker ps -aq)

echo -e "--------------- DELETING ALL DOCKER CONTAINERS ---------------\n\n"
# shellcheck disable=SC2046
sudo docker rm $(sudo docker ps -aq)

echo -e "-------------------- MAVEN BUILD RUNNING ---------------------\n\n"
mvn -f pom.xml clean install -U

echo -e "----------------- BUILDING DOCKER CONTAINER ------------------\n\n"
sudo docker build -t my_bot .

echo -e "------------------ RUNNING DOCKER CONTAINER ------------------\n\n"
sudo docker run -d --restart=always --name=my_bot --network=host my_bot

echo -e "---------------- RUNNING DOCKER LOGS FOLLOWING ---------------\n\n"
docker logs -f my_bot

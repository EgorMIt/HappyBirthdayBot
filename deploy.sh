#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-1.17.0-openjdk-amd64

# shellcheck disable=SC2046
sudo docker stop $(sudo docker ps -aq)
# shellcheck disable=SC2046
sudo docker rm $(sudo docker ps -aq)

mvn -f pom.xml clean install -U

sudo docker build -t my_bot .

sudo docker run -d --restart=always --name=my_bot --network=host my_bot
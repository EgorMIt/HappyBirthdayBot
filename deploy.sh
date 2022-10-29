#!/bin/bash

# shellcheck disable=SC2046
docker stop $(docker ps -aq)
docker rm $(docker ps -aq)

mvn clean install

docker build -t my_bot .

docker run -d --restart=always --name=my_bot --network=host my_bot
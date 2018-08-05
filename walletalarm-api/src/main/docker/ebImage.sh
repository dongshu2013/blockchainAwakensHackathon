#!/usr/bin/env bash
echo "Enter zip file name > "
read filename
cd ./../../../
mvn clean package -DskipTests=true
cd src/main/docker
cp ././../../../target/walletalarm-api-1.0-SNAPSHOT.jar walletalarm-api-1.0-SNAPSHOT.jar
cp ././../resources/ApiConfiguration.eb.test.yml ApiConfiguration.eb.test.yml
zip ${filename}.zip Dockerfile walletalarm-api-1.0-SNAPSHOT.jar ApiConfiguration.eb.test.yml
rm walletalarm-api-1.0-SNAPSHOT.jar
rm ApiConfiguration.eb.test.yml
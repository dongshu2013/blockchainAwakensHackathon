FROM java:8-jre
MAINTAINER docker-maven-plugin@jolokia.org
EXPOSE 8080 8081
ADD walletalarm-api-1.0-SNAPSHOT.jar walletalarm-api-1.0-SNAPSHOT.jar
ADD ApiConfiguration.eb.test.yml ApiConfiguration.eb.test.yml
CMD java -jar walletalarm-api-1.0-SNAPSHOT.jar server ApiConfiguration.eb.test.yml
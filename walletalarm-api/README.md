# walletalarm-api
[![build status](https://gitlab.com/vikrampatankar/walletalarm-api/badges/master/build.svg)](https://gitlab.com/vikrampatankar/walletalarm-api/commits/master)

[![coverage report](https://gitlab.com/vikrampatankar/walletalarm-api/badges/master/coverage.svg)](https://gitlab.com/vikrampatankar/walletalarm-api/commits/master)

**Run API server locally** 

This project needs a running instance of MySQL locally. Run the db scripts in the 'db' folder before running the api

<code>
mvn install:install-file -Dfile=lib/dropwizard-jobs-core-3.0.1-SNAPSHOT.jar -DgroupId=de.spinscale.dropwizard -DartifactId=dropwizard-jobs-core -Dversion=3.0.1-SNAPSHOT -Dpackaging=jar

java -jar target/walletalarm-api-1.0-SNAPSHOT.jar server src/test/resources/ApiConfiguration.local.test.yml
</code>

**Create aws elastic beanstalk (eb) image**

<code>
sh src/main/docker/ebImage.sh
</code>

This will create a zip file which can be used directly to launch a web server as a single docker instance on eb
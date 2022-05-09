# Job allocation-service

This service reads new messages from kafka and initiats the job  allocation flow by finding nearest riders, sorting and then sending it forward for broadcasting.

This service is responsible to deal with rider status
-------------------
### What you’ll need
A favorite text editor or IDE
JDK 8 or later
Install Gradle
Install Redis

-------------
### Build Java code
Now we are behind few step.
1. update mongoDb uri to localhost mongo address in application-local.yml file.
2. add  "-Dspring.profiles.active=local" in cmd as arguments
3. run cmd gradle clean build or gradlew clean build.
4. Kafka is also required, therefore please run the kafka server,bootstrap etc on local.

-----
  BUILD SUCCESSFUL
  Project ran successfully.

##Nexus credential configuration
In `~/.m2/settings.xml` add the below configuration
```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
<servers>
    <server>
        <id>rider-maven-release</id>
        <username>username</username>
        <password>password</password>
    </server>
</servers>
</settings>
```

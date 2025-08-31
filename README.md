# NICE Homework Task

Implement a Web API (with bonus). Quick run & test guide.

## Prerequisites
- Java 17+
- Maven 3.9+

## Option 1 - Start the app (dev)
```powershell'''
mvn spring-boot:run

# Default port is 8080, to run on a different port : 
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"


### Send a few example requests after the app is running(If you changed the port, update the URL accordingly)
##1. ResetPasswordTask
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8080/suggestTask `
  -ContentType "application/json" `
  -Body '{"utterance":"reset password","userId":"u1","sessionId":"s1","timestamp":"2025-08-21T12:00:00Z"}'
##2. CheckOrderStatusTask
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8080/suggestTask `
  -ContentType "application/json" `
  -Body '{"utterance":"check order","userId":"u1","sessionId":"s1","timestamp":"2025-08-21T12:00:00Z"}'
##3. NoTaskFound
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8080/suggestTask `
  -ContentType "application/json" `
  -Body '{"utterance":"hello there","userId":"u1","sessionId":"s1","timestamp":"2025-08-21T12:00:00Z"}'
##4. Validation error (400)
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8080/suggestTask `
  -ContentType "application/json" `
  -Body '{"utterance":"","userId":"","sessionId":"","timestamp":null}'


## Option 2 - Build & run the JAR
mvn clean package
java -jar target/nice-homework-task-0.0.1-SNAPSHOT.jar

# Default port is 8080, to run on a different port
java -jar target/nice-homework-task-0.0.1-SNAPSHOT.jar --server.port=9090 

Then call the API as shown above


# Run all tests (unit + integration + retry)
mvn clean verify

#Run only unit test (NiceHomeworkTaskServiceTest file)
mvn "-Dtest=NiceHomeworkTaskServiceTest" test

#Run only integration tests (SuggestTaskIT file)
mvn "-Dit.test=SuggestTaskIT" failsafe:integration-test failsafe:verify

#Run only Retry tests (NiceHomeworkTaskServiceRetryTest file)
mvn "-Dtest=NiceHomeworkTaskServiceRetryTest" test










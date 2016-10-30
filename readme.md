# Cloud Pocket 2

Cloud Pocket is application written in java (using Spring Boot framework) and designed for storing files in a cloud.
The main goal is simplicity. Easy to deploy and run and easy to use.

## Deploy the application

### Run as standalone application

Cloud Pocket 2 is standalone application. To start server just run:

`java -jar <cloud-pocket-app-version>.war`

Also you can provide some configuration as command line arguments. For example:

`java -jar <cloud-pocket-app-version>.war --server.port=80`

will start server on 80 port (but make sure that the port is free).

For more details see Spring Boot application properties documentation and `application.properties` file.

### Deploy on Apache Tomcat

You can deploy the application as a regular `*.war` file.

##### _It is strictly recommended to configure secured connection_


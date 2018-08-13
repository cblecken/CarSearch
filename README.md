# Car Search via API

Simple car data is extracted from permanent storage (SQL table) and loaded into 
a search index (elastic search). The indexed data is then exposed as a REST Service,
with two simple search options (by make and by year). The development setup is 
local but the final setup will run in AWS. 

## Getting Started

You can build the project with gradle. After you clone you can build this via 
gradlew bootRun. For publish to AWS you can use bootJar or bootWar targets.

The project is written in Java (using Spring Boot) and an IDE is recommended. I used
eclipse, largely because of the nice AWS plugin which does not require so often
context switching. 

### Prerequisites

You need elastic search (v6.3.0), the code uses the new RestHighLevelClient API 
(what a name) and a backend database such as mysql or postgresql. 


### Installing

You should run gradlew in the root directory to get the project built.

gradlew build

You can also import into an IDE. 

### Notes

Initially i wanted to use spring data elasticsearch, but that was requiring the 
original ES transport client. After i had issues running in the Cloud i switched 
to the new RestHighLevelClient, which is not as simple to use as a similar boot plugin
but allowed detailed control.

The micro-service was mapped though the AWS API Gateway to be a managed service.

The service can be access via https://pjvvpf8lgb.execute-api.us-west-2.amazonaws.com/beta

There are two APIs : 
Query by year : /api/year
Query by make : /api/make

The query string is a http parameter 'query'. 

Examples : 
https://pjvvpf8lgb.execute-api.us-west-2.amazonaws.com/beta/api/year?query=2000
https://pjvvpf8lgb.execute-api.us-west-2.amazonaws.com/beta/api/make?query=Chevrolet

## Running the tests

The tests are run with : gradlew test

## Deployment

The whole application can be deployed on AWS as a micro-service. The AWS project used
here was Beanstalk, but any way should work fine. There are several devops artifacts here
in the resources directory. SafeDemoStarter.template is a AWS Cloud formation script 
to create the necessary Beanstalk environment and application from scratch.
The other script SafeDemo.template is the full AWS CF script, which also creates
the RDS resource and the elastic search instance. This does not (yet) have a secure production
variant adding an VPC setup with a Bastion and the necessary security groups, but this is
for another day. 

## Built With

* [Spring Boot](https://spring.io/projects/spring-boot) - The framework used
* [Gradle](https://gradle.org/) - Dependency Management

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning


## Authors

* **Carsten Blecken** 

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License


## Acknowledgments




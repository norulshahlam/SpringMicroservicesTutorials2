# 2021 edition - Java, Spring Framework, Spring Boot, Spring Cloud, Microservices, IntelliJ, REST, JPA, Maven, Zuul,Ribbon

`Topics covered:`  
Externalized Configuration  
Reading configuration from application.properties (from GIT)  
Reading configuration from application.properties (from individual services)  
Integrating Microservices with Spring Cloud Config Server  
Refreshing configuration without restarting microservices  
Dynamic routing using Netflix Zuul  
Client Side Load Balancing using Netflix Ribbon  

# Part 2

## Externalized Configuration

[https://www.udemy.com/course/java-spring-boot-microservices-project-for-beginners/learn/lecture/18187592#content]

We have learn how to store various configuration parameters in application.properties. The challenge is everytime when we need to make changes, we have to modify application.properties and re-run the application. This is impossible in production setup where the entire project is in a jar file

Spring Boot lets you externalize your configuration so that you can work with the same application code in different environments. You can use properties files, YAML files, environment variables, and command-line arguments to externalize configuration. Property values can be injected directly into your beans by using the @Value annotation, accessed through Spring’s Environment abstraction, or be bound to structured objects through @ConfigurationProperties.

One of the ways is to use Spring Cloud Config Server. it can manage all properties file - global and individual services

## Reading configuration from application.properties (from GIT)

[https://www.udemy.com/course/java-spring-boot-microservices-project-for-beginners/learn/lecture/18191256#content]

Setup your ConfigServer to add git url containing property files. 

The only thing to make this into config server is to add this in pom:

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>


`run this:`

EurekaServer - http://localhost:8761/  
ConfigServer - http://localhost:8888/

Then run Payment and Order service `GLOBAL` url (without running the app)

http://localhost:8888/order-service/default,  
http://localhost:8888/payment-service/default


We now can access properties of OrderService and PaymentService without running the services. We can also make changes and see the changes reflected upon browser refresh.

## Reading configuration from application.properties (from individual services)

[https://www.udemy.com/course/java-spring-boot-microservices-project-for-beginners/learn/lecture/18192000#content]

Stop EurekaServer & ConfigServer. run:

OrderService - http://localhost:8001/  
PaymentService - http://localhost:8002/

and see the message. We see that the data is fetch from the local services instead of git


## Integrating Microservices with Spring Cloud Config Server

[https://www.udemy.com/course/java-spring-boot-microservices-project-for-beginners/learn/lecture/18192604#content]

a. by simply adding @EnableDiscoveryClient in both services, we r now conecting Order and PAyment services to the config server. re run all services (in order) n run the url to see the diff response:

EurekaServer - http://localhost:8761/  
ConfigServer - http://localhost:8888/

See the changes in these 2 response below. We see that the data is fetch from the git instead of local services. ConfigServer ignore the local service properties and prioritize git properties.

OrderService - http://localhost:8001/  
PaymentService - http://localhost:8002/

#### note

if u cant connect to config server, try to add this dependency:

    <dependency>
        <groupId>org.springframework.cloud</groupId> 	
        <artifactId>spring-cloud-starter-bootstrap</artifactId>
    </dependency>

b. Currently the welcome-message variable is using the global property file - FutureXSkill. By defining an application.name in local services, we can connect to the specific service in the git url:

spring.application.name=order-service  
spring.application.name=payment-service

re run both services n run the above url to see the diff response in 'welcome-message':

OrderService - http://localhost:8001/  
PaymentService - http://localhost:8002/

c. now try to change both services in git application.properties. run the config server to see the immediate changes:

http://localhost:8888/order-service/default  
http://localhost:8888/payment-service/default

However there is no immedaite change in local service:

Order service: http://localhost:8001/  
Payment service: http://localhost:8002/

You have to restart/refresh each local service to reflect the changes.

## Refreshing configuration without restarting microservices

To allow changs without resrtarting, simply do this:

    a. add `@RefreshScope` in both controllers.
    b. add in in each proerty file:
        management.endpoints.web.exposure.include=*
    c. run this IN POSTMAN to refresh the changes:

POST METHOD http://localhost:8002/actuator/refresh  
POST METHOD http://localhost:8001/actuator/refresh

it will return the properties that was changed

reload to see the changes reflected without restarting the server:

Order service: http://localhost:8001/  
Payment service: http://localhost:8002/


*******************************************************

# Part 3

## Dynamic routing using Netflix Zuul

The Netflix Zuul service provides dynamic routing. Using Zuul in your app enables your services to use the information from the Eureka service directory to reach other services.

Routing is an integral part of a microservice architecture. For example, / may be mapped to your web application, /api/users is mapped to the user service and /api/shop is mapped to the shop service. Zuul is a JVM-based router and server-side load balancer from Netflix.

`create a gateway service`

to make a service a gateway service, simply add in pom:

        <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-zuul</artifactId>
		</dependency>

and add this annotion in your main method:

        @EnableDiscoveryClient
        @EnableZuulProxy

    then start & run the following url in order:
        
EurekaServer - http://localhost:8761/  
OrderService - http://localhost:8001/  
PaymentService - http://localhost:8002/  

GatewayService to access payment & Order service -   
    http://localhost:8080/payment-service  
    http://localhost:8080/order-service  

you will see that this url give the same result:

 GatewayService - http://localhost:8080/payment-service &  
 PaymentService - http://localhost:8002/

if payment service has /home resource, you can access thru gateway service too:

http://localhost:8080/payment-service/home == http://localhost:8002/home


## Client Side Load Balancing using Netflix Ribbon

[https://www.udemy.com/course/java-spring-boot-microservices-project-for-beginners/learn/lecture/18149244#content]

Netflix Ribbon is a Part of Netflix Open Source Software (Netflix OSS). It is a cloud library that provides the client-side load balancing. ... The Ribbon mainly provides client-side load balancing algorithms. It is a client-side load balancer that provides control over the behavior of HTTP and TCP client.

    Run EurekaServer

`Running multiple instance of the same service`

    run CurrencyService - http://localhost:8004/. 
    This will simply show the port number

if you are using vscode, do this additional steps:

    1. change the port number to 8005 in application.properties
    2. run this in terminal:

        mvn spring-boot:run -Dspring-boot.run.profiles=CurrencyService

        sources: 
        
        [https://stackoverflow.com/questions/42390860/configure-active-profile-in-springboot-via-maven]

    3. go to EurekaServer and you will see there's 2 instances running 

    4. run both url
        http://localhost:8004/  
        http://localhost:8005/

`Setting up load balancer`

Create a new service to configure it as load balancer. simply just add in pom:

        <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
        </dependency>
    
Also some configurations in the controller.

Make sure theres 3 instance running in Eureka server

Run LoadBalancer service and refresh few times. You will see the port number change

    http://localhost:8080/

************** END OF TUTORIAL *****************
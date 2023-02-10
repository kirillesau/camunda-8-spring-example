# Camunda 8 with Spring Boot example
For this example, I am using the [camunda-community-hub](https://github.com/camunda-community-hub) approach as described [here](https://github.com/camunda-community-hub/spring-zeebe).

## Prerequisite

The example requires a [Camunda account](https://docs.camunda.io/docs/next/guides/getting-started/) with access to the [Camunda Platform Console](https://docs.camunda.io/docs/components/console/introduction-to-console/).

## How to run

### Maven
>Note: You can only run it if the credentials have been filled in correctly! See [Modify application.properties
](README.md#modify-applicationproperties)

```shell
./mvnw package spring-boot:run
```

After that instances can be started with the following variable content:

````json
{
  "reference": "C8_12345",
  "amount": 100.00,
  "cardNumber": "1234567812345678",
  "cardExpiry": "12/2023",
  "cardCVC": "123"
}
````
See the [official guide](https://docs.camunda.io/docs/components/modeler/web-modeler/start-instance/) for further information. 

The terminal will show the following output:

````console
2023-02-10 10:43:00.167 TRACE 31393 --- [pool-4-thread-1] d.k.c.job.CreditCardJobWorker            : start Job for Bpmn-id: paymentProcess
2023-02-10 10:43:00.168 TRACE 31393 --- [pool-4-thread-1] d.k.c.service.CreditCardService          : Charging Credit Card: 1234567812345678
2023-02-10 10:43:00.168  INFO 31393 --- [pool-4-thread-1] d.k.c.service.CreditCardService          : Credit Card Data: 
2023-02-10 10:43:00.168  INFO 31393 --- [pool-4-thread-1] d.k.c.service.CreditCardService          : - reference: C8_12345
2023-02-10 10:43:00.169  INFO 31393 --- [pool-4-thread-1] d.k.c.service.CreditCardService          : - amount: 100.0
2023-02-10 10:43:00.169  INFO 31393 --- [pool-4-thread-1] d.k.c.service.CreditCardService          : - cardExpiry: 12/2023
2023-02-10 10:43:00.169  INFO 31393 --- [pool-4-thread-1] d.k.c.service.CreditCardService          : - cardCVC: 123
````

## Implementation tips
### Modify application.properties
Fill the [application.properties](src/main/resources/application.properties)-File with your individual values. Client credentials can be generated in the Camunda 8 Console. See [Set up client connection credentials](https://docs.camunda.io/docs/guides/setup-client-connection-credentials/) for further information.
```properties
zeebe.client.cloud.region=<your region>
zeebe.client.cloud.clusterId=<your cluster id>
zeebe.client.cloud.clientId=<your client id>
zeebe.client.cloud.clientSecret=<your client secret>
```

### Modify pom.xml
Add additional dependencies in your pom.xml:

```xml
...
<properties>
    ...
    <spring-zeebe.version>8.1.5</spring-zeebe.version>
</properties>
...
<dependencies>
...
  <dependency>
      <groupId>io.camunda</groupId>
      <artifactId>spring-zeebe-starter</artifactId>
      <version>${spring-zeebe.version}</version>
  </dependency>
  <dependency>
      <groupId>io.camunda</groupId>
      <artifactId>spring-zeebe-test</artifactId>
      <version>${spring-zeebe.version}</version>
      <scope>test</scope>
  </dependency>
</dependencies>  
...
```

### Modify Application-class

```java
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableZeebeClient
@SpringBootApplication
@EnableScheduling
@Deployment(resources = "classpath*:*.bpmn")
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

}

```
### Add a new bmpn file

Add your .bmpn file to [resources](src/main/resources).

See [CreditCardProcess.bpmn](src/main/resources/CreditCardProcess.bpmn) for example.

### Create a new worker

```java
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import org.springframework.stereotype.Component;

@Component
public class ExampleWorker {
    
    // simple worker
    @JobWorker(type = "exampleType")
    public void handle(final ActivatedJob job) {
        // implement your code here
    }

    // worker with 1 variable
    @JobWorker(type = "exampleType2")
    public void handle2(final ActivatedJob job, @Variable String variableName) {
        // implement your code here
    }
    
    // worker with x variables as object
    @JobWorker(type = "exampleType3")
    public void handle3(final ActivatedJob job, @VariablesAsType MyCustomObject customObject) {
        // implement your code here
    }
    
}
```

### Testing

```java
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;

@SpringBootTest
@ZeebeSpringTest
class ExampleTest {

    @Autowired
    private ZeebeClient zeebe;

    @Autowired
    private ZeebeTestEngine zeebeTestEngine;

    @Test
    void creditCardShouldBeCharged() {
        MyObject myObjectVariables = new MyObject();

        // start a process instance
        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand() //
                .bpmnProcessId("paymentProcess").latestVersion() //
                .variables(myObjectVariables) //
                .send().join();

        // wait for process completion
        waitForProcessInstanceCompleted(processInstance);

        // assert that process is finished and my EndEvent is passed
        assertThat(processInstance)
                .hasPassedElement("My_EndEvent_Id")
                .isCompleted();
    }
}
```
package de.kirill.camunda8springexample.job;

import de.kirill.camunda8springexample.service.CreditCardService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ZeebeSpringTest
class CreditCardJobWorkerTest {

    @Autowired
    private ZeebeClient zeebe;

    @Autowired
    private ZeebeTestEngine zeebeTestEngine; // TODO: Intellij shows error

    @MockBean
    private CreditCardService creditCardService;

    @Test
    void creditCardShouldBeCharged() {
        CreditCardVariables cardVariables = new CreditCardVariables("C8_12345", "100.00", "1234567812345678", "12/2023", "123");

        // start a process instance
        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand() //
                .bpmnProcessId("paymentProcess").latestVersion() //
                .variables(cardVariables) //
                .send().join();

        waitForProcessInstanceCompleted(processInstance);

        Mockito.verify(creditCardService).chargeCreditCard();
        Mockito.verifyNoMoreInteractions(creditCardService);

        assertThat(processInstance)
                .hasPassedElement("EndEvent_PaymentDone")
                .isCompleted();
    }
}
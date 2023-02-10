package de.kirill.camunda8springexample.job;

import de.kirill.camunda8springexample.service.CreditCardService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class CreditCardJobWorker {

    private final CreditCardService creditCardService;

    @JobWorker(type = "chargeCreditCard")
    public void handleChargeCreditCard(final ActivatedJob job, @VariablesAsType CreditCardVariables creditCardVariables) {
        log.trace("start Job for Bpmn-id: " + job.getBpmnProcessId());
        creditCardService.chargeCreditCard(creditCardVariables);
    }

}

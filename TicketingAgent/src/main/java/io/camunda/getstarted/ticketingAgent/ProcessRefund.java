package io.camunda.getstarted.ticketingAgent;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@SpringBootApplication
@EnableZeebeClient
public class ProcessRefund {

    @Autowired
    private PaymentStorageService paymentStorageService;

    public static void main(String[] args) {
        SpringApplication.run(ProcessRefund.class, args);
    }

    @ZeebeWorker(type = "process_refund")
    public void processRefund(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        String transactionId = (String) variablesAsMap.get("transactionId");

        if (transactionId == null || transactionId.isEmpty()) {
            System.out.println("Error: Transaction ID is missing.");
            Map<String, Object> failureVars = new HashMap<>();
            failureVars.put("refundStatus", "Failed - Missing transaction ID");

            client.newCompleteCommand(job.getKey())
                    .variables(failureVars)
                    .send()
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not complete job", throwable);
                    });

            return;
        }

        Map<String, Object> paymentDetails = paymentStorageService.getPaymentDetails(transactionId);

        if (paymentDetails == null) {
            System.out.println("Error: No payment details found for transaction ID: " + transactionId);
            Map<String, Object> failureVars = new HashMap<>();
            failureVars.put("refundStatus", "Failed - No payment details found");

            client.newCompleteCommand(job.getKey())
                    .variables(failureVars)
                    .send()
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not complete job", throwable);
                    });

            return;
        }

        // Refund successful
        Map<String, Object> successVars = new HashMap<>(paymentDetails);
        successVars.put("refundStatus", "Success");

        client.newCompleteCommand(job.getKey())
                .variables(successVars)
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job", throwable);
                });
    }
}

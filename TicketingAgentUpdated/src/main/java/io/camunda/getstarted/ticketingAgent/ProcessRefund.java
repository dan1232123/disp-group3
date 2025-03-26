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
    private IssueRepository issueRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public static void main(String[] args) {
        SpringApplication.run(ProcessRefund.class, args);
    }

    @ZeebeWorker(type = "process_refund")
    public void processRefund(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        String issueId = (String) variablesAsMap.get("issueId");

        Transaction transaction = transactionRepository.findByIssueIdAndTransactionDescription(issueId, "repair");

        String transactionId = transaction.getId();


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




        // Refund successful
        System.out.println("Process Refund: " + transactionId);
        transaction.setTransactionStatus("refunded");
        transactionRepository.save(transaction);


        client.newCompleteCommand(job.getKey())
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job", throwable);
                });
    }
}

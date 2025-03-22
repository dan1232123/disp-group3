package io.camunda.getstarted.ticketingAgent;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@SpringBootApplication
@EnableZeebeClient
public class ProcessRefund {

    public static void main(String[] args) {
        SpringApplication.run(Worker.class, args);
    }

    // Worker to process refunds
    @ZeebeWorker(type = "process_refund")
    public void processRefund(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();

        // Get the resolution choice
        Object resolutionObj = variablesAsMap.get("resolutionChoice");
        String resolutionChoice = resolutionObj != null ? resolutionObj.toString() : "";

        // Get the original payment method
        String paymentMethod = (String) variablesAsMap.get("originalPaymentMethod");

        Map<String, Object> variables = new HashMap<>();
        variables.put("resolutionChoice", resolutionChoice);
        variables.put("paymentMethod", paymentMethod);

        // Check if refund should be processed
        if ("0".equals(resolutionChoice)) {
            System.out.println("Processing full refund using " + paymentMethod + "...");

            String refundAmount = (String) variablesAsMap.get("refund_amount");
            variables.put("refundAmount", refundAmount);

            try {
                switch (paymentMethod.toLowerCase()) {
                    case "card":
                        variables.put("cardName", variablesAsMap.get("card_name"));
                        variables.put("cardNumber", variablesAsMap.get("card_number"));
                        variables.put("expiryDate", variablesAsMap.get("expiry_date"));
                        variables.put("cvv", variablesAsMap.get("cvv"));
                        break;

                    case "paypal":
                        variables.put("payPalEmail", variablesAsMap.get("payPalEmail"));
                        break;

                    case "banktransfer":
                        variables.put("sortCode", variablesAsMap.get("sortCode"));
                        variables.put("accountNumber", variablesAsMap.get("accountNumber"));
                        break;

                    default:
                        System.out.println("Invalid payment method found: " + paymentMethod);
                        variables.put("refundStatus", "Failed - Invalid original payment method");
                        break;
                }

                System.out.println("Refund approved for: " + refundAmount + " via " + paymentMethod);
                variables.put("refundStatus", "Success");

            } catch (Exception e) {
                e.printStackTrace();
                client.newFailCommand(job.getKey())
                        .retries(job.getRetries() - 1)
                        .send();
                return;
            }

        } else if ("1".equals(resolutionChoice)) {
            // Skip refund and proceed to repair
            System.out.println("Skipping refund. Proceeding with free follow-up repair.");
            variables.put("refundSkipped", true);
        } else {
            System.out.println("Invalid resolution choice.");
            variables.put("error", "Invalid resolution choice");
        }

        // Complete the task and update process variables
        client.newCompleteCommand(job.getKey())
                .variables(variables)
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job", throwable);
                });
    }
}
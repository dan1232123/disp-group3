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

        // Extract process variables from Camunda
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();

        String cardName = (String) variablesAsMap.get("card_name"); // No validation needed
        String cardNumber = (String) variablesAsMap.get("card_number");
        String expiryDate = (String) variablesAsMap.get("expiry_date"); // Combined MM/YY format
        String cvv = (String) variablesAsMap.get("cvv");
        String refundAmount = (String) variablesAsMap.get("refund_amount"); // Refund amount to process

        Map<String, Object> variables = new HashMap<>();
        variables.put("cardName", cardName);
        variables.put("refundAmount", refundAmount);

        try {
            boolean isCardValid = validateCardNumber(cardNumber);
            boolean isCVVValid = validateCVV(cvv);
            boolean isExpiryValid = validateExpiry(expiryDate);

            variables.put("validCard", isCardValid);
            variables.put("validCVV", isCVVValid);
            variables.put("validExpiry", isExpiryValid);

            if (isCardValid && isCVVValid && isExpiryValid) {
                System.out.println("Refund approved for: " + refundAmount);
            } else {
                System.out.println("Refund failed due to invalid card details.");
            }

            client.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not complete job", throwable);
                    });

        } catch (Exception e) {
            int retries = job.getRetries() - 1;
            e.printStackTrace();

            client.newFailCommand(job.getKey())
                    .retries(retries)
                    .send();
        }
    }

    private boolean validateCardNumber(String cardNumber) {
        if (cardNumber == null || !cardNumber.matches("\\d{13,19}")) {
            return false;
        }
        return luhnCheck(cardNumber);
    }

    private boolean validateCVV(String cvv) {
        return cvv != null && cvv.matches("\\d{3,4}");
    }

    private boolean validateExpiry(String expiryDate) {
        if (expiryDate == null || !expiryDate.matches("^(0[1-9]|1[0-2])/(\\d{2}|\\d{4})$")) {
            return false;
        }

        try {
            String[] parts = expiryDate.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);

            if (year < 100) {
                year += 2000; // Convert 2-digit year to 4-digit
            }

            YearMonth expiry = YearMonth.of(year, month);
            YearMonth currentMonth = YearMonth.now();
            YearMonth nextWeek = currentMonth.plusMonths(1);

            return expiry.isAfter(currentMonth) && expiry.isAfter(nextWeek);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(cardNumber.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}

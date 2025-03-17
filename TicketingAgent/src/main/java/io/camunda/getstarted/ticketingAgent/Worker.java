package io.camunda.getstarted.ticketingAgent;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@SpringBootApplication
@EnableZeebeClient
public class Worker {

    public static void main(String[] args) {
        SpringApplication.run(Worker.class, args);
    }

    @ZeebeWorker(type = "collect_payment")
    public void processPayment(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        String paymentMethod = (String) variablesAsMap.get("paymentMethod");
        Map<String, Object> variables = new HashMap<>();
        variables.put("collect_payment", paymentMethod);

        try {
            boolean isValid = validatePaymentDetails(paymentMethod, variablesAsMap, variables);

            if (isValid) {
                System.out.println("Payment details are valid.");
            } else {
                System.out.println("Payment validation failed.");
            }

            variables.put("validPaymentDetails", isValid);

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

    private boolean validatePaymentDetails(String paymentMethod, Map<String, Object> inputVars, Map<String, Object> outputVars) {
        boolean isValid = false;

        switch (paymentMethod.toLowerCase()) {
            case "paypal":
                String paypalEmail = (String) inputVars.get("payPalEmail");
                isValid = validateEmail(paypalEmail);
                outputVars.put("paypalEmail", paypalEmail);
                outputVars.put("validPaypalEmail", isValid);
                break;

            case "bankTransfer":
                String sortCode = (String) inputVars.get("sortCode");
                String accountNumber = (String) inputVars.get("accountNumber");
                isValid = validateBankDetails(sortCode, accountNumber);
                outputVars.put("sortCode", sortCode);
                outputVars.put("accountNumber", accountNumber);
                outputVars.put("validBankDetails", isValid);
                break;

            case "card":
                String cardNumber = (String) inputVars.get("cardNumber");
                String expiryMonth = (String) inputVars.get("expirationDate");
                String expiryYear = (String) inputVars.get("expirationDate");
                String cvv = (String) inputVars.get("cvv");
                isValid = validateCardDetails(cardNumber, expiryMonth, expiryYear, cvv);
                outputVars.put("cardNumber", cardNumber);
                outputVars.put("expiryMonth", expiryMonth);
                outputVars.put("expiryYear", expiryYear);
                outputVars.put("cvv", cvv);
                outputVars.put("validCardDetails", isValid);
                break;

            default:
                System.out.println("Invalid payment method selected.");
        }

        return isValid;
    }

    private boolean validateEmail(String email) {
        return email != null && Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches();
    }

    private boolean validateBankDetails(String sortCode, String accountNumber) {
        return sortCode != null && sortCode.matches("\\d{6}") &&
                accountNumber != null && accountNumber.matches("\\d{8,12}");
    }

    private boolean validateCardDetails(String cardNumber, String expiryMonth, String expiryYear, String cvv) {
        return validateCardNumber(cardNumber) &&
                validateExpiry(expiryMonth, expiryYear) &&
                validateCVV(cvv);
    }

    private boolean validateCardNumber(String cardNumber) {
        return cardNumber != null && cardNumber.matches("\\d{13,19}") && luhnCheck(cardNumber);
    }

    private boolean validateCVV(String cvv) {
        return cvv != null && cvv.matches("\\d{3,4}");
    }

    private boolean validateExpiry(String expiryMonth, String expiryYear) {
        try {
            int month = Integer.parseInt(expiryMonth);
            int year = Integer.parseInt(expiryYear);
            if (month < 1 || month > 12 || year < LocalDate.now().getYear()) {
                return false;
            }
            return YearMonth.of(year, month).isAfter(YearMonth.now());
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

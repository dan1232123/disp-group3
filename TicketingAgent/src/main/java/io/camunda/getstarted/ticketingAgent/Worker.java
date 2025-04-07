package io.camunda.getstarted.ticketingAgent;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

@SpringBootApplication
@EnableZeebeClient
public class Worker {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private IssueRepository issueRepository;

    public static void main(String[] args) {
        SpringApplication.run(Worker.class, args);
    }

    @ZeebeWorker(type = "collect_payment") // Handles job type of collect_payment
    public void processPayment(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        String paymentMethod = (String) variablesAsMap.get("paymentMethod");
        Map<String, Object> variables = new HashMap<>();
        variables.put("collect_payment", paymentMethod);

        Random random = new Random();

        String issueId = (String) variablesAsMap.get("issueId");

        Boolean isMember = (boolean) variablesAsMap.get("isMember");

        try {
            // Validates payment method
            boolean isValid = validatePaymentDetails(paymentMethod, variablesAsMap, variables);
            variables.put("validPaymentDetails", isValid ? "1" : "0");

            Transaction newTransaction = new Transaction();

            String name = (String) variablesAsMap.get("customerName");
            String email = (String) variablesAsMap.get("customerEmailAddress");
            String type = (String) variablesAsMap.get("type");
            Integer amount = random.nextInt(100) + 1;

            newTransaction.setCustomerName(name);
            newTransaction.setCustomerEmailAddress(email);
            newTransaction.setPaymentMethod(paymentMethod);
            newTransaction.setTransactionDescription(type);
            newTransaction.setIssueId(String.valueOf(issueId));

            if (isValid) {
                System.out.println("Payment is valid,details saved with transaction ID: " + newTransaction.getId());

                // Update the relevant issue status
                Issue issue = issueRepository.findById(issueId).orElse(null);
                if (issue != null) {
                    issue.setIssueStatus(type + " Paid");
                    issueRepository.save(issue);
                    System.out.println("Issue status updated to " + type + " paid");
                } else {
                    System.out.println("Issue not found with ID: " + issueId);
                }

                // Apply a 10% discount for members
                if (isMember) {
                    newTransaction.setAmount(String.valueOf(amount * 0.9));

                    variables.put("Payment_Message", "User is a Member, Discount Applied.\n" +
                            "Payment details are valid.");

                } else {
                    variables.put("Payment_Message", "Payment details are valid.");
                    newTransaction.setAmount(String.valueOf(amount));
                }
                newTransaction.setTransactionStatus("Completed");

                // If validation fails
            } else {
                System.out.println("Payment validation failed.");

                if (isMember) {
                    variables.put("Payment_Message", "User is a Member.\n" + "Payment details are invalid please try again.");

                } else {
                    variables.put("Payment_Message", "Payment details are invalid.");
                }

                newTransaction.setTransactionStatus("Failed");
                // 🔹 Reset payment fields for re-entry
                variables.put("cardNumber", "");
                variables.put("expirationDate", "");
                variables.put("cvv", "");
                variables.put("sortCode", "");
                variables.put("accountNumber", "");
                variables.put("payPalEmail", "");
            }

            // Save the transaction
            transactionRepository.save(newTransaction);
            System.out.println("New transaction created");

            // Save the refund amount for the repairs
            if (type == "repairs") {
                variables.put("refundAmount", String.valueOf(amount));
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

    // Validates different payment methods with appropriate traces
    private boolean validatePaymentDetails(String paymentMethod, Map<String, Object> inputVars, Map<String, Object> outputVars) {
        boolean isValid = false;

        switch (paymentMethod.toLowerCase()) {
            case "paypal":
                String paypalEmail = (String) inputVars.get("payPalEmail");
                isValid = validateEmail(paypalEmail);
                outputVars.put("payPalEmail", paypalEmail);
                break;

            case "banktransfer":
                String sortCode = (String) inputVars.get("sortCode");
                String accountNumber = (String) inputVars.get("accountNumber");
                isValid = validateBankDetails(sortCode, accountNumber);
                outputVars.put("sortCode", sortCode);
                outputVars.put("accountNumber", accountNumber);
                break;

            case "card":
                String cardNumber = (String) inputVars.get("cardNumber");
                String expirationDate = (String) inputVars.get("expirationDate");
                String cvv = (String) inputVars.get("cvv");
                isValid = validateCardDetails(cardNumber, expirationDate, cvv);
                outputVars.put("cardNumber", cardNumber);
                outputVars.put("expirationDate", expirationDate);
                outputVars.put("cvv", cvv);
                break;

            default:
                outputVars.put("Payment_Message", "Invalid payment method.");
                return false;
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

    private boolean validateCardDetails(String cardNumber, String expirationDate, String cvv) {
        return validateCardNumber(cardNumber) &&
                validateExpiry(expirationDate) &&
                validateCVV(cvv);
    }

    private boolean validateCardNumber(String cardNumber) {
        return cardNumber != null && cardNumber.matches("\\d{13,19}") && luhnCheck(cardNumber);
    }

    private boolean validateCVV(String cvv) {
        return cvv != null && cvv.matches("\\d{3,4}");
    }

    private boolean validateExpiry(String expirationDate) {
        try {
            String[] parts = expirationDate.split("-");
            if (parts.length != 3) {
                return false;
            }
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
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

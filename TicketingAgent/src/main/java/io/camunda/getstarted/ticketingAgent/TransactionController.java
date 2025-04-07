package io.camunda.getstarted.ticketingAgent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping("/inputTransaction")
    public Transaction inputTransaction(@RequestBody TransactionDto transactionDto) {
        // Create new transaction
        Transaction newTransaction = new Transaction();
        newTransaction.setCustomerName(transactionDto.getCustomerName());
        newTransaction.setCustomerEmailAddress(transactionDto.getCustomerEmailAddress());
        newTransaction.setTransactionDate(transactionDto.getTransactionDate());
        newTransaction.setAmount(transactionDto.getAmount());
        newTransaction.setPaymentMethod(transactionDto.getPaymentMethod());
        newTransaction.setTransactionStatus(transactionDto.getTransactionStatus());
        newTransaction.setTransactionDescription(transactionDto.getTransactionDescription());
        newTransaction.setIssueId(transactionDto.getIssueId());

        // Save to the repository and return the saved transaction
        return transactionRepository.save(newTransaction);
    }
}

class TransactionDto {
    private String transactionId;
    private String customerName;
    private String customerEmailAddress;
    private String transactionDate;
    private String amount;
    private String paymentMethod;
    private String transactionStatus;
    private String transactionDescription;
    private String issueId;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmailAddress() { return customerEmailAddress; }
    public void setCustomerEmailAddress(String customerEmailAddress) { this.customerEmailAddress = customerEmailAddress; }

    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }

    public String getTransactionDescription() { return transactionDescription; }
    public void setTransactionDescription(String transactionDescription) { this.transactionDescription = transactionDescription; }

    public String getIssueId() { return issueId; }
    public void setIssueId(String issueId) { this.issueId = issueId; }
}

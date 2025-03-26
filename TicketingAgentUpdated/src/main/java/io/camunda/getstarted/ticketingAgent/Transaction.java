package io.camunda.getstarted.ticketingAgent;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    private String id;
    private String customerName;
    private String customerEmailAddress;
    private String transactionDate;
    private String amount;
    private String paymentMethod;
    private String transactionStatus;
    private String transactionDescription;
    private String issueId;

    // Getters and Setters

    public Transaction() {
        this.id = UUID.randomUUID().toString();
        this.transactionDate = LocalDate.now().toString();
    }

    public String getId() {
        return id;
    }

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

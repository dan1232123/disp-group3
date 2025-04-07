package io.camunda.getstarted.ticketingAgent;

import org.springframework.data.jpa.repository.JpaRepository;

// Extends Jpa repository to perform standard crud operations
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // retrieves issue based on its id and transaction description
    Transaction findByIssueIdAndTransactionDescription(String issueId, String type);
}

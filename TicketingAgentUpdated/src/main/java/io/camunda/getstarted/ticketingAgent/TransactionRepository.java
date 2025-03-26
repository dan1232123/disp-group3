package io.camunda.getstarted.ticketingAgent;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Transaction findByIssueIdAndTransactionDescription(String issueId, String type);
}

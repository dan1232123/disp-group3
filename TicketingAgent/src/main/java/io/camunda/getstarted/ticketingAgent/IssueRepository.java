package io.camunda.getstarted.ticketingAgent;
import org.springframework.data.jpa.repository.JpaRepository;

// issue repo extending jpa repo for basic crud operations
public interface IssueRepository extends JpaRepository<Issue, String> {

}
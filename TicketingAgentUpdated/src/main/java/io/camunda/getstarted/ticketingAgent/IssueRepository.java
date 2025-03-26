package io.camunda.getstarted.ticketingAgent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, String> {

}
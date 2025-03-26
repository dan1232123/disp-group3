package io.camunda.getstarted.ticketingAgent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
    boolean existsByEmail(String email);
}
package io.camunda.getstarted.ticketingAgent;
import org.springframework.data.jpa.repository.JpaRepository;

// Member repo extending jpa repository for basic crud operations
public interface MemberRepository extends JpaRepository<Member, String> {
    // Checks if a member exists by email
    boolean existsByEmail(String email);
}
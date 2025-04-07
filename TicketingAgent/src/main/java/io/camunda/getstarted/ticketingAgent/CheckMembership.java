package io.camunda.getstarted.ticketingAgent;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableZeebeClient
public class CheckMembership {

    @Autowired
    private MemberRepository memberRepository;

    @ZeebeWorker(type = "check_membership") // Job of type check membership
    public void checkMembership(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        String emailAddress = (String) variablesAsMap.get("customerEmailAddress");

        System.out.println(emailAddress);

        // Checks if the user exists by email in the members table
        boolean isMember = memberRepository.existsByEmail(emailAddress);

        System.out.println(isMember);

        Map<String, Object> outputVariables = new HashMap<>();
        // set a global variable in camunda for if the user is a member
        outputVariables.put("isMember", isMember);

        client.newCompleteCommand(job.getKey())
                .variables(outputVariables)
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Failed to complete job", throwable);
                });
    }
}
package io.camunda.getstarted.ticketingAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@SpringBootApplication
@EnableZeebeClient
public class RegisterMembership {

    @Autowired
    private MemberRepository memberRepository;

    @ZeebeWorker(type = "register_membership") // Make sure it matches BPMN job type
    public void applyDiscount(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        String name = (String) variablesAsMap.get("registerName");
        String email = (String) variablesAsMap.get("registerEmail");
        String phone = (String) variablesAsMap.get("registerPhone");

        boolean isMember = false;

        try {
            if (!memberRepository.existsByEmail(email)) {
                Member newMember = new Member();
                newMember.setName(name);
                newMember.setEmail(email);
                newMember.setPhone(phone);
                memberRepository.save(newMember);
                isMember = true;
                System.out.println("New member created");
            }
        } catch (Exception e) {
            System.out.println("Error while registering member: " + e.getMessage());
        }

        Map<String, Object> outputVariables = new HashMap<>();
        outputVariables.put("isMember", isMember);

        client.newCompleteCommand(job.getKey())
                .variables(outputVariables)
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Failed to complete job", throwable);
                });

        // get email address


    }
}
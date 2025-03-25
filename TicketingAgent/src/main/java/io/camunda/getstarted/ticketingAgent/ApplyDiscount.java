package io.camunda.getstarted.ticketingAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@SpringBootApplication
@EnableZeebeClient
public class ApplyDiscount {

    @ZeebeWorker(type = "apply_discount") // Make sure it matches BPMN job type
    public void applyDiscount(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        Map<String, Object> variables = new HashMap<>();


        // 🛠 Debugging: Print all received variables
        System.out.println("Received variables in apply_discount: " + variablesAsMap);

        try {
            // Check if 'check_member' exists
            if (!variablesAsMap.containsKey("check_member")) {
                System.out.println("ERROR: 'check_member' variable is missing.");
                client.newFailCommand(job.getKey()).retries(job.getRetries() - 1).send();
                return;
            }

            // Convert string to boolean safely
            String checkMemberStr = variablesAsMap.get("check_member").toString();
            boolean isMember = Boolean.parseBoolean(variablesAsMap.get("check_member").toString());


            // Store converted value
            variables.put("check_member", isMember);

            if (isMember) {
                System.out.println("User is a member. Applying discount.");
                variables.put("discountApplied", true);
            } else {
                System.out.println("User is not a member. No discount.");
                variables.put("discountApplied", false);
            }

            // Complete the job with updated variables
            client.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not complete job", throwable);
                    });

        } catch (Exception e) {
            int retries = job.getRetries() - 1;
            e.printStackTrace();
            client.newFailCommand(job.getKey()).retries(retries).send();
        }
    }

}
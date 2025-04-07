package io.camunda.getstarted.ticketingAgent;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import io.camunda.getstarted.ticketingAgent.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@SpringBootApplication
@EnableZeebeClient
public class ActivateWarranty {

    @Autowired
    private IssueRepository issueRepository;

    @ZeebeWorker(type = "activate_warranty") // job of type activate warranty
    public void activateWarranty(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();

        System.out.println("Activate Warranty");

        String issueId = variablesAsMap.get("issueId").toString();

        // find the issue using the issue id
        Issue issue = issueRepository.findById(issueId).orElse(null);
        if (issue != null) {
            // set the date the warranty expires to two weeks into the future
            String date = LocalDate.now().plusWeeks(2).toString();
            // Activate the warranty
            issue.setWarrantyActiveUntil(date);
            issueRepository.save(issue);
            System.out.println("Warranty Activated");
        } else {
            System.out.println("Issue not found with ID: " + issueId);
        }

        client.newCompleteCommand(job.getKey()).send();

    }
}
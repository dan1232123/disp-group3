package io.camunda.getstarted.ticketingAgent;

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
public class UpdateStatus {

    @Autowired
    private IssueRepository issueRepository;

    @ZeebeWorker(type = "update_status") // Make sure it matches BPMN job type
    public void applyDiscount(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();

        String status = variablesAsMap.get("status").toString();
        String issueId = variablesAsMap.get("issueId").toString();
        Issue issue = issueRepository.findById(issueId).orElse(null);  // Fetch the issue by ID
        if (issue != null) {
            issue.setIssueStatus(status);  // Update the status
            issueRepository.save(issue);  // Save the updated issue
            System.out.println("Issue status updated to " + status);
        } else {
            System.out.println("Issue not found with ID: " + issueId);
        }

        client.newCompleteCommand(job.getKey()).send();

    }
}
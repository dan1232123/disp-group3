package io.camunda.getstarted.ticketingAgent;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
@EnableZeebeClient
public class UpdateStatus {

    @Autowired
    private IssueRepository issueRepository;

    @ZeebeWorker(type = "update_status") // Job for updating status of issue
    public void updateStatus(final JobClient client, final ActivatedJob job) {
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
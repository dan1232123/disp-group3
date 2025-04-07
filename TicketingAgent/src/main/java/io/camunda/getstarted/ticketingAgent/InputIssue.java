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
public class InputIssue {

    @Autowired
    private IssueRepository issueRepository;

    @ZeebeWorker(type = "input_issue") // Job of type input_issue
    public void inputIssue(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();

        for (Map.Entry<String, Object> entry : variablesAsMap.entrySet()) {
            String variableName = entry.getKey();
            Object variableValue = entry.getValue();

            System.out.println("Variable Name: " + variableName + ", Value: " + variableValue);
        }

        // Create a new issue
        String email = (String) variablesAsMap.get("customerEmailAddress");
        String name = (String) variablesAsMap.get("customerName");
        String phone = (String) variablesAsMap.get("customerPhoneNumber");
        String vehicleRegistrationNumber = (String) variablesAsMap.get("customerVehicleRegistrationNumber");
        String vehicleModel = (String) variablesAsMap.get("customerVehicleModel");
        String vehicleYear = String.valueOf(variablesAsMap.get("customerVehicleYear"));
        String mileage = String.valueOf(variablesAsMap.get("customerMileage"));
        String issueDescription = (String) variablesAsMap.get("customerIssueDescription");
        String serviceType = (String) variablesAsMap.get("customerServiceType");
        String dateOfIssueOccurrence = (String) variablesAsMap.get("customerDateOfIssueOccurrence");
        String isTowingServiceRequired = String.valueOf(variablesAsMap.get("customerIsTowingServiceRequired"));
        String additionalComments = (String) variablesAsMap.get("customerAdditionalComments");

        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
        System.out.println("Phone: " + phone);
        System.out.println("Vehicle registration number: " + vehicleRegistrationNumber);
        System.out.println("Vehicle model: " + vehicleModel);
        System.out.println("Vehicle year: " + vehicleYear);
        System.out.println("Mileage: " + mileage);
        System.out.println("Issue description: " + issueDescription);
        System.out.println("Service type: " + serviceType);


        try {
            // Create new issue entity
            Issue newIssue = new Issue();
            newIssue.setEmailAddress(email);
            newIssue.setName(name);
            newIssue.setPhoneNumber(phone);
            newIssue.setVehicleRegistrationNumber(vehicleRegistrationNumber);
            newIssue.setVehicleModel(vehicleModel);
            newIssue.setVehicleYear(vehicleYear);
            newIssue.setMileage(mileage);
            newIssue.setIssueDescription(issueDescription);
            newIssue.setServiceType(serviceType);
            newIssue.setDateOfIssueOccurrence(dateOfIssueOccurrence);
            newIssue.setIsTowingServiceRequired(isTowingServiceRequired);
            newIssue.setAdditionalComments(additionalComments);

            // Save to the repository
            issueRepository.save(newIssue);
            System.out.println("New issue created");

            Map<String, Object> outputVariables = new HashMap<>();
            outputVariables.put("issueId", String.valueOf(newIssue.getId()));


            client.newCompleteCommand(job.getKey()).variables(outputVariables).send()
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not complete job", throwable);
                    });
        } catch (Exception e) {
            System.out.println("Error while creating issue: " + e.getMessage());
        }
    }
}

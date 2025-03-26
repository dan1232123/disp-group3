package io.camunda.getstarted.ticketingAgent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    @Autowired
    private IssueRepository issueRepository;

    @PostMapping("/inputIssue")
    public Issue inputIssue(@RequestBody IssueDto issueDto) {
        // Create new issue
        Issue newIssue = new Issue();
        newIssue.setEmailAddress(issueDto.getEmailAddress());
        newIssue.setName(issueDto.getName());
        newIssue.setPhoneNumber(issueDto.getPhoneNumber());
        newIssue.setVehicleRegistrationNumber(issueDto.getVehicleRegistrationNumber());
        newIssue.setVehicleModel(issueDto.getVehicleModel());
        newIssue.setVehicleYear(issueDto.getVehicleYear());
        newIssue.setMileage(issueDto.getMileage());
        newIssue.setIssueDescription(issueDto.getIssueDescription());
        newIssue.setServiceType(issueDto.getServiceType());
        newIssue.setDateOfIssueOccurrence(issueDto.getDateOfIssueOccurrence());
        newIssue.setIsTowingServiceRequired(issueDto.getIsTowingServiceRequired());
        newIssue.setAdditionalComments(issueDto.getAdditionalComments());
        newIssue.setIssueStatus(issueDto.getIssueStatus());

        return issueRepository.save(newIssue);
    }
}

class IssueDto {
    private String emailAddress;
    private String name;
    private String phoneNumber;
    private String vehicleRegistrationNumber;
    private String vehicleModel;
    private String vehicleYear;
    private String mileage;
    private String issueDescription;
    private String serviceType;
    private String dateOfIssueOccurrence;
    private String isTowingServiceRequired;
    private String additionalComments;
    private String issueStatus;

    // Getters and Setters
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVehicleRegistrationNumber() {
        return vehicleRegistrationNumber;
    }

    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleYear() {
        return vehicleYear;
    }

    public void setVehicleYear(String vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDateOfIssueOccurrence() {
        return dateOfIssueOccurrence;
    }

    public void setDateOfIssueOccurrence(String dateOfIssueOccurrence) {
        this.dateOfIssueOccurrence = dateOfIssueOccurrence;
    }

    public String getIsTowingServiceRequired() {
        return isTowingServiceRequired;
    }

    public void setIsTowingServiceRequired(String isTowingServiceRequired) {
        this.isTowingServiceRequired = isTowingServiceRequired;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }
}

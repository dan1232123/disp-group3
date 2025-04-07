package io.camunda.getstarted.ticketingAgent;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "issues")
public class Issue {
    @Id
    private String id;

    private String emailAddress;

    private String phoneNumber;
    private String name;
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
    private String warrantyActiveUntil;

    public Issue() {
        this.id = UUID.randomUUID().toString();
        this.issueStatus = "new";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

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

    public String getWarrantyActiveUntil() {
        return warrantyActiveUntil;
    }

    public void setWarrantyActiveUntil(String warrantyActiveUntil) {
        this.warrantyActiveUntil = warrantyActiveUntil;
    }
}

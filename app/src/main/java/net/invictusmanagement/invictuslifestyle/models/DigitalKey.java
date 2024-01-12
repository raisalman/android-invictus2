package net.invictusmanagement.invictuslifestyle.models;

import java.util.ArrayList;
import java.util.Date;

public class DigitalKey extends ModelBase {

    private Date fromUtc;
    private Date toUtc;
    private String recipient;
    private String email;
    private String notes;
    private String key;
    private String phoneNumber;
    private Integer serviceTypeId;
    private boolean isRevoked;
    private boolean isQuickKey;
    private boolean toPackageCenter;
    private ArrayList<GuestEntryDoor> selectedEntry;

    public Date getFromUtc() {
        return fromUtc;
    }

    public void setFromUtc(Date fromUtc) {
        this.fromUtc = fromUtc;
    }

    public Date getToUtc() {
        return toUtc;
    }

    public void setToUtc(Date toUtc) {
        this.toUtc = toUtc;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean revoked) {
        isRevoked = revoked;
    }

    public boolean isQuickKey() {
        return isQuickKey;
    }

    public void setQuickKey(boolean quickKey) {
        isQuickKey = quickKey;
    }

    public ArrayList<GuestEntryDoor> getSelectedEntry() {
        return selectedEntry;
    }

    public void setSelectedEntry(ArrayList<GuestEntryDoor> selectedEntry) {
        this.selectedEntry = selectedEntry;
    }

    public boolean isToPackageCenter() {
        return toPackageCenter;
    }

    public void setToPackageCenter(boolean toPackageCenter) {
        this.toPackageCenter = toPackageCenter;
    }
}
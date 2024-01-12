package net.invictusmanagement.invictuslifestyle.models;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ServiceKey extends ModelBase implements Serializable {

    private String companyId;
    public long vendorMappingId;
    private Date fromUtc;
    private Date toUtc;
    private String recipient;
    private String technicianName;
    private boolean noEndDate;
    private boolean fullDay;
    private String email;
    private String phoneNumber;
    private Date start;
    private Date end;
    private String notes;
    private String key;
    private String mapUrl;
    private String[] mapUrls;
    private String mapImage;
    private File mapFile;
    private String fileName;
    private String repeatType;
    private String repeatValueList;
    private ArrayList<GuestEntryDoor> selectedEntry;
    private String selectedEntryJSON;
    private boolean isRevoked;

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


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public boolean isNoEndDate() {
        return noEndDate;
    }

    public void setNoEndDate(boolean noEndDate) {
        this.noEndDate = noEndDate;
    }

    public boolean isFullDay() {
        return fullDay;
    }

    public void setFullDay(boolean fullDay) {
        this.fullDay = fullDay;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public String getMapImage() {
        return mapImage;
    }

    public void setMapImage(String mapImage) {
        this.mapImage = mapImage;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean revoked) {
        isRevoked = revoked;
    }

    public String[] getMapUrls() {
        return mapUrls;
    }

    public void setMapUrls(String[] mapUrls) {
        this.mapUrls = mapUrls;
    }

    public File getMapFile() {
        return mapFile;
    }

    public void setMapFile(File mapFile) {
        this.mapFile = mapFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRepeatValueList() {
        return repeatValueList;
    }

    public void setRepeatValueList(String repeatValueList) {
        this.repeatValueList = repeatValueList;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public ArrayList<GuestEntryDoor> getSelectedEntry() {
        return selectedEntry;
    }

    public void setSelectedEntry(ArrayList<GuestEntryDoor> selectedEntry) {
        this.selectedEntry = selectedEntry;
    }

    public String getSelectedEntryJSON() {
        return selectedEntryJSON;
    }

    public void setSelectedEntryJSON(String selectedEntryJSON) {
        this.selectedEntryJSON = selectedEntryJSON;
    }
}
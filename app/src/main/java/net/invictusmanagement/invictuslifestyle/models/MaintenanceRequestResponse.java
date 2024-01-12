package net.invictusmanagement.invictuslifestyle.models;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MaintenanceRequestResponse extends ModelBase implements Serializable {

    private Date closedUtc;
    private String title;
    private String description;
    private MaintenanceRequest.Status status;
    private Boolean needPermission;
    private String residentName;
    private String unitNbr;
    private String notes;
    private int locationId;
    private List<MaintenanceRequesFiles> maintenanceRequestFiles;
    private List<File> uploadMaintenanceRequestFiles;
    public long companyId;
    public String recipient;
    public String technicianName;

    public Date getClosedUtc() {
        return closedUtc;
    }

    public void setClosedUtc(Date closedUtc) {
        this.closedUtc = closedUtc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Boolean getNeedPermission() {
        return needPermission;
    }

    public void setNeedPermission(Boolean needPermission) {
        this.needPermission = needPermission;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }

    public String getUnitNbr() {
        return unitNbr;
    }

    public void setUnitNbr(String unitNbr) {
        this.unitNbr = unitNbr;
    }

    public MaintenanceRequest.Status getStatus() {
        return status;
    }

    public void setStatus(MaintenanceRequest.Status status) {
        this.status = status;
    }

    public List<MaintenanceRequesFiles> getMaintenanceRequestFiles() {
        return maintenanceRequestFiles;
    }

    public void setMaintenanceRequestFiles(List<MaintenanceRequesFiles> maintenanceRequestFiles) {
        this.maintenanceRequestFiles = maintenanceRequestFiles;
    }

    public List<File> getUploadMaintenanceRequestFiles() {
        return uploadMaintenanceRequestFiles;
    }

    public void setUploadMaintenanceRequestFiles(List<File> uploadMaintenanceRequestFiles) {
        this.uploadMaintenanceRequestFiles = uploadMaintenanceRequestFiles;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }
}

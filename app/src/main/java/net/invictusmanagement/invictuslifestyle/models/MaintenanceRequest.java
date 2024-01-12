package net.invictusmanagement.invictuslifestyle.models;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MaintenanceRequest extends ModelBase implements Serializable {

    private Date closedUtc;
    private String title;
    private String description;
    private Status status;
    private Boolean needPermission;
    private String notes;
    private String companyId;
    private String technicianName;
    private List<MaintenanceRequesFiles> maintenanceRequestFiles;
    private List<File> uploadMaintenanceRequestFiles;
    private String deleteMaintenanceRequestFiles;

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getNeedPermission() {
        return needPermission;
    }

    public void setNeedPermission(Boolean needPermission) {
        this.needPermission = needPermission;
    }

    public List<File> getUploadMaintenanceRequestFiles() {
        return uploadMaintenanceRequestFiles;
    }

    public void setUploadMaintenanceRequestFiles(List<File> uploadMaintenanceRequestFiles) {
        this.uploadMaintenanceRequestFiles = uploadMaintenanceRequestFiles;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<MaintenanceRequesFiles> getMaintenanceRequestFiles() {
        return maintenanceRequestFiles;
    }

    public void setMaintenanceRequestFiles(List<MaintenanceRequesFiles> maintenanceRequestFiles) {
        this.maintenanceRequestFiles = maintenanceRequestFiles;
    }

    public String getDeleteMaintenanceRequestFiles() {
        return deleteMaintenanceRequestFiles;
    }

    public void setDeleteMaintenanceRequestFiles(String deleteMaintenanceRequestFiles) {
        this.deleteMaintenanceRequestFiles = deleteMaintenanceRequestFiles;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public enum Status {

        @SerializedName("1")
        Active(1),

        @SerializedName("2")
        Closed(2),

        @SerializedName("3")
        Requested(3),

        @SerializedName("4")
        RequestedToClose(4),

        @SerializedName("5")
        NotSolve(5);

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}

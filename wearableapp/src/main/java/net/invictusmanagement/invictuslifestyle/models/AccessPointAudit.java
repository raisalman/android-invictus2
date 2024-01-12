package net.invictusmanagement.invictuslifestyle.models;

public class AccessPointAudit {
    private long accessPointId;
    private Boolean isValid;
    private String entryName;

    public long getAccessPointId() {
        return accessPointId;
    }

    public void setAccessPointId(long accessPointId) {
        this.accessPointId = accessPointId;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }
}
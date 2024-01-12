package net.invictusmanagement.invictuslifestyle.models;

public class OpenAccessPoint {
    private long id;
    private Boolean isSilent;
    private Boolean isVideoAccess;
    private String operator;
    private String entryName;
    private String deviceType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean getSilent() {
        return isSilent;
    }

    public void setSilent(Boolean silent) {
        isSilent = silent;
    }

    public Boolean getVideoAccess() {
        return isVideoAccess;
    }

    public void setVideoAccess(Boolean videoAccess) {
        isVideoAccess = videoAccess;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
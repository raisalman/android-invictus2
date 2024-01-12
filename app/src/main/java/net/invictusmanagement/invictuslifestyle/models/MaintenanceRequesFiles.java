package net.invictusmanagement.invictuslifestyle.models;

import java.io.Serializable;

public class MaintenanceRequesFiles implements Serializable {
    public String maintenanceRequestId;
    public Boolean isImage;
    public Boolean isBeforeSolve;
    public String maintenanceRequestImageSrc;
    public Integer id;
    public long duration = 0;
    public String createdUtc;
    public Boolean deleted;
    public Boolean isFromAd = false;
}
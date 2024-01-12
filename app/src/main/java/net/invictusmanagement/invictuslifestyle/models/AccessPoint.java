package net.invictusmanagement.invictuslifestyle.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AccessPoint extends ModelBase implements Serializable {

    private String name;
    private Type type;
    private Boolean isFavorite;
    private String locationName;
    private long locationId;
    //Invictus = 1 , OpenPath = 2, Brivo = 3
    private int operator;
    private String pdkPanelId;
    private int displayOrder;
    private long userAccessPointId;

    //use internally, 0 = reset, 1= success, 2 = fail, 3= processing
    public int unlockingStatus = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public int getOperator() {
        return operator;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public String getPdkPanelId() {
        return pdkPanelId;
    }

    public void setPdkPanelId(String pdkPanelId) {
        this.pdkPanelId = pdkPanelId;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public long getUserAccessPointId() {
        return userAccessPointId;
    }

    public void setUserAccessPointId(long userAccessPointId) {
        this.userAccessPointId = userAccessPointId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public enum Type {

        @SerializedName("1")
        Pedestrian(1),

        @SerializedName("2")
        Vehicle(2);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}

package net.invictusmanagement.invictuslifestyle.models;

import com.google.gson.annotations.SerializedName;

public class AccessPoint extends ModelBase {

    private String name;
    private Type type;
    private Boolean isFavorite;
    //Invictus = 1 , OpenPath = 2, Brivo = 3
    private int operator;
    private String pdkPanelId;
    private int displayOrder;
    private long userAccessPointId;

    //Brivo use internally, 0 = reset, 1= success, 2 = fail, 3= processing
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

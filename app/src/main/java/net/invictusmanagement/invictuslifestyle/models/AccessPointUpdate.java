package net.invictusmanagement.invictuslifestyle.models;

public class AccessPointUpdate extends ModelBase {

    private String applicationUserId;
    private long accessPointId;
    private int operator;
    private boolean isFavorite;
    private int displayOrder;

    public String  getApplicationUserId() {
        return applicationUserId;
    }

    public void setApplicationUserId(String applicationUserId) {
        this.applicationUserId = applicationUserId;
    }

    public long getAccessPointId() {
        return accessPointId;
    }

    public void setAccessPointId(long accessPointId) {
        this.accessPointId = accessPointId;
    }

    public int getOperator() {
        return operator;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}

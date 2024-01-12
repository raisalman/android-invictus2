package net.invictusmanagement.invictuslifestyle.models;

public class AccessPointFavUnFavRequest {
    private long userAccessPointId;
    private boolean isFavorite;
    private String operator;
    private long accessPointId;


    public long getUserAccessPointId() {
        return userAccessPointId;
    }

    public void setUserAccessPointId(long userAccessPointId) {
        this.userAccessPointId = userAccessPointId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public long getAccessPointId() {
        return accessPointId;
    }

    public void setAccessPointId(long accessPointId) {
        this.accessPointId = accessPointId;
    }
}
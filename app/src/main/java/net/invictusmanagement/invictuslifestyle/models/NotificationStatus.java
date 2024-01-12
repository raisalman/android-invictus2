package net.invictusmanagement.invictuslifestyle.models;

public class NotificationStatus extends ModelBase {
    private int applicationUserId;
    private Boolean accessPoints;
    private Boolean coupons;
    private Boolean digitalKey;
    private Boolean mainRequests;
    private Boolean healthVideo;
    private Boolean bulletinBoard;
    private Boolean voiceMail;
    private Boolean amenities;
    private Boolean deleted;
    private Boolean chat = false;

    public int getApplicationUserId() {
        return applicationUserId;
    }

    public void setApplicationUserId(int applicationUserId) {
        this.applicationUserId = applicationUserId;
    }

    public Boolean getAccessPoints() {
        return accessPoints;
    }

    public void setAccessPoints(Boolean accessPoints) {
        this.accessPoints = accessPoints;
    }

    public Boolean getCoupons() {
        return coupons;
    }

    public void setCoupons(Boolean coupons) {
        this.coupons = coupons;
    }

    public Boolean getDigitalKey() {
        return digitalKey;
    }

    public void setDigitalKey(Boolean digitalKey) {
        this.digitalKey = digitalKey;
    }

    public Boolean getMainRequests() {
        return mainRequests;
    }

    public void setMainRequests(Boolean mainRequests) {
        this.mainRequests = mainRequests;
    }

    public Boolean getHealthVideo() {
        return healthVideo;
    }

    public void setHealthVideo(Boolean healthVideo) {
        this.healthVideo = healthVideo;
    }

    public Boolean getBulletinBoard() {
        return bulletinBoard;
    }

    public void setBulletinBoard(Boolean bulletinBoard) {
        this.bulletinBoard = bulletinBoard;
    }

    public Boolean getVoiceMail() {
        return voiceMail;
    }

    public void setVoiceMail(Boolean voiceMail) {
        this.voiceMail = voiceMail;
    }

    public Boolean getAmenities() {
        return amenities;
    }

    public void setAmenities(Boolean amenities) {
        this.amenities = amenities;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getChat() {
        return chat;
    }

    public void setChat(Boolean chat) {
        this.chat = chat;
    }
}
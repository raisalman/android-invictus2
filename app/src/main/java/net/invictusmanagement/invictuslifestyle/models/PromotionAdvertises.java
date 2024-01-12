package net.invictusmanagement.invictuslifestyle.models;

public class PromotionAdvertises extends ModelBase {
    private String businessId;
    private String promotionId;
    private String advertiseFileSrc;
    private boolean isImage;
    private boolean deleted;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public String getAdvertiseFileSrc() {
        return advertiseFileSrc;
    }

    public void setAdvertiseFileSrc(String advertiseFileSrc) {
        this.advertiseFileSrc = advertiseFileSrc;
    }

    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
}
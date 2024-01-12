package net.invictusmanagement.invictuslifestyle.models;

import java.util.Date;
import java.util.List;

public class Promotion extends ModelBase {
    private String name;
    private String description;
    private Date fromUtc;
    private Date toUtc;
    private Boolean isFavorite;
    private Boolean isAnytimeCoupon = false;
    private List<PromotionAdvertises> promotionAdvertises;

    public List<PromotionAdvertises> getPromotionAdvertises() {
        return promotionAdvertises;
    }

    public void setPromotionAdvertises(List<PromotionAdvertises> promotionAdvertises) {
        this.promotionAdvertises = promotionAdvertises;
    }

    public Boolean getAnytimeCoupon() {
        return isAnytimeCoupon;
    }

    public void setAnytimeCoupon(Boolean anytimeCoupon) {
        isAnytimeCoupon = anytimeCoupon;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public Date getToUtc() {
        return toUtc;
    }

    public void setToUtc(Date toUtc) {
        this.toUtc = toUtc;
    }

    public Date getFromUtc() {
        return fromUtc;
    }

    public void setFromUtc(Date fromUtc) {
        this.fromUtc = fromUtc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
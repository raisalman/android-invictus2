package net.invictusmanagement.invictuslifestyle.models;

import java.util.Date;
import java.util.List;

public class PromotionFav extends ModelBase {
    public String name;
    public String description;
    public String businessId;
    public String businessName;
    public String favAdvertismentFileUrl;
    public Date fromUtc;
    public Date toUtc;
    public Boolean isFavorite;
    public Boolean isAnytimeCoupon = false;
    public List<PromotionAdvertises> promotionAdvertises;
}
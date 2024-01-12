package net.invictusmanagement.invictuslifestyle.models;

import java.util.Date;
import java.util.List;

public class BulletinBoard {
    public long id;
    public Date createdUtc;
    public String applicationUserId;
    public String marketPlaceCategoryName;
    public List<MarketPlaceImage> marketPlaceImages;
    public String title;
    public String description;
    public String price;
    public String condition;
    public String availableDate;
    public Date fromUtc;
    public Date toUtc;
    public Boolean isFavorite;
    public Boolean isMyPost;
    public Boolean isService;
    public Boolean deleted;
    public String marketPlaceCategoryId;
    public String phone;
    public String email;
    public String residentName;
    public int conditionType;
    public int contectType;
    public String contactTime;
    public Boolean isSoldOut;
    public Boolean isClosed;
    public Boolean isApproved;
    public Boolean isHourPrice;
}

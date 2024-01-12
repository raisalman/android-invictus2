package net.invictusmanagement.invictuslifestyle.models;

import java.io.File;
import java.util.Date;
import java.util.List;

public class MarketPlacePost {
    public long id;
    public Date createdUtc;
    public String applicationUserId;
    public String MarketPlaceCategoryId;
    public String Title;
    public String Description;
    public String Price;
    public int ConditionType;
    public String AvailableDate;
    public int ContactType;
    public String ContactTime;
    public Boolean IsSoldOut;
    public Boolean IsService;
    public Boolean IsHourPrice;
    public Boolean IsClosed;
    public Boolean IsApproved;
    public List<File> MarketPlaceImages;
}
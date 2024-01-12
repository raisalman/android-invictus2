package net.invictusmanagement.invictuslifestyle.models;

import java.util.Date;

public class Topic extends ModelBase {
    public int locationId;
    public String topic;
    public String description;
    public String status;
    public String appStatus;
    public String applicationUserId;
    public String adminUserId;
    public int residentUnreadCount;
    public int adminUnreadCount;
    public Date updatedUtc;
}
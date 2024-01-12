package net.invictusmanagement.invictuslifestyle.models;

import java.util.ArrayList;
import java.util.Date;

public class ChatTopic extends ModelBase {
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
    public boolean isByResident;
    public String residentName;
    public String applicationUser;
    public int isAlreadyAccepted;
    public String sender;
    public String location;
    public ArrayList<String> chatMessages;
    public boolean deleted;
}
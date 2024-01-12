package net.invictusmanagement.invictuslifestyle.models;

import java.util.Date;

public class MessageData {
    public long chatRequestId;
    public long applicationUserId;
    public long adminUserId;
    public int messageType;
    public int chatMessageId;
    public boolean isMyMessage;
    public boolean isLocalImage = false;
    public boolean isReceivedImage = false;
    public boolean isRead = false;
    public String residentName;
    public String locationId;
    public String message;
    public String description;
    public String sender;
    public String attachmentUrl;
    public String topic;
    public Date chatRequestDate;
    public Date createdUtc;
}

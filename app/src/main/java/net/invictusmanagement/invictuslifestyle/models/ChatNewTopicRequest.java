package net.invictusmanagement.invictuslifestyle.models;

public class ChatNewTopicRequest {
    public int status = 1;
    public long applicationUserId;
    public long locationId;
    public String topic;
    public String description;
    public String sender;
    public long adminUserId;
}
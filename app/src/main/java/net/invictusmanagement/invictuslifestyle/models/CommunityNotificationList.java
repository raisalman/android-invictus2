package net.invictusmanagement.invictuslifestyle.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class CommunityNotificationList extends ModelBase implements Serializable {
    public String title;
    public String message;
    public int locationId;
    public int notificationCategoryId;
    public String imageUrl;
    public String redirectionURL;
    public String location;
    public ArrayList<NotificationUsers> applicationUserNotifications;
    public int[] selectedRecipientIds;
    public boolean deleted;

    public class NotificationUsers implements Serializable {
        public String user;
        public long userId;
        public long notificationId;
        public boolean isRead;
        public Date readOn;
        public String notification;
    }
}
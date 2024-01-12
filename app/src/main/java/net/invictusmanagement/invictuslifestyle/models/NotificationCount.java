package net.invictusmanagement.invictuslifestyle.models;

public class NotificationCount {
    private int unreadNotificationCount;
    private int unreadVoiceMailCount;

    public int getUnreadNotificationCount() {
        return unreadNotificationCount;
    }

    public void setUnreadNotificationCount(int unreadNotificationCount) {
        this.unreadNotificationCount = unreadNotificationCount;
    }

    public int getUnreadVoiceMailCount() {
        return unreadVoiceMailCount;
    }

    public void setUnreadVoiceMailCount(int unreadVoiceMailCount) {
        this.unreadVoiceMailCount = unreadVoiceMailCount;
    }
}
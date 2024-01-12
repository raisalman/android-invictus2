package net.invictusmanagement.invictuslifestyle.enum_utils;

public enum NotificationStatusEnum {
    AccessPoints("1"), Coupons("2"), DigitalKey("3"),
    MainRequests("4"), HealthVideo("5"), BulletinBoard("6"), VoiceMail("7"), Amenities("8"), Chat("9");

    String key;

    NotificationStatusEnum(String str) {
        key = str;
    }

    public String value() {
        return this.key;
    }
}
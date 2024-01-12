package net.invictusmanagement.invictuslifestyle.enum_utils;

public enum NewDeviceRequestStatus {
    Pending(1), Accepted(2), Rejected(3), Activated(4);
    int key;

    NewDeviceRequestStatus(int str) {
        key = str;
    }

    public int value() {
        return this.key;
    }
}
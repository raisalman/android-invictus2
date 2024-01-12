package net.invictusmanagement.invictuslifestyle.enum_utils;

public enum ChatRequestStatus {
    Pending("1"), Open("2"), Close("3");
    String key;

    ChatRequestStatus(String str) {
        key = str;
    }

    public String value() {
        return this.key;
    }
}
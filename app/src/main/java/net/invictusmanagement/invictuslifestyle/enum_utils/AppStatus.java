package net.invictusmanagement.invictuslifestyle.enum_utils;

public enum AppStatus {
    Offline("1"), Active("2"), Away("3");
    String key;

    AppStatus(String str) {
        key = str;
    }

    public String value() {
        return this.key;
    }
}
package net.invictusmanagement.invictuslifestyle.enum_utils;

public enum DeviceType {
    Mobile("Mobile"), Watch("Watch");
    String key;

    DeviceType(String str) {
        key = str;
    }

    public String value() {
        return this.key;
    }
}
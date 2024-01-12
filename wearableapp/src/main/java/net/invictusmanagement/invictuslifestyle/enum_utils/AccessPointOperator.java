package net.invictusmanagement.invictuslifestyle.enum_utils;

public enum AccessPointOperator {
    Invictus("Invictus"),
    OpenPath("OpenPath"),
    Brivo("Brivo"),
    PDK("PDK");
    String key;

    AccessPointOperator(String str) {
        key = str;
    }

    public String value() {
        return this.key;
    }
}
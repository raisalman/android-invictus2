package net.invictusmanagement.invictuslifestyle.models;

public class CheckCameraAccess {
    public String deviceId;
    public String operator;

    public CheckCameraAccess(String deviceId, String een) {
        this.deviceId = deviceId;
        this.operator = een;
    }
}
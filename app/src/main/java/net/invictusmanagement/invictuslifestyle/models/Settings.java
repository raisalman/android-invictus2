package net.invictusmanagement.invictuslifestyle.models;

public class Settings {
    private Integer high;
    private Integer low;
    private String fan;
    private String mode;
    private String high_min;
    private String low_max;


    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public Integer getLow() {
        return low;
    }

    public void setLow(Integer low) {
        this.low = low;
    }

    public String getFan() {
        return fan;
    }

    public void setFan(String fan) {
        this.fan = fan;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getHigh_min() {
        return high_min;
    }

    public void setHigh_min(String high_min) {
        this.high_min = high_min;
    }

    public String getLow_max() {
        return low_max;
    }

    public void setLow_max(String low_max) {
        this.low_max = low_max;
    }
}
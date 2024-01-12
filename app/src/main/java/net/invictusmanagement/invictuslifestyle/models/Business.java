package net.invictusmanagement.invictuslifestyle.models;

import java.util.List;

public class Business extends ModelBase {
    private String name;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private String advertiseUrl;
    private boolean hasAnyTimeCoupons;
    private double latitude;
    private double longitude;
    private List<Promotion> promotions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdvertiseUrl() {
        return advertiseUrl;
    }

    public void setAdvertiseUrl(String advertiseUrl) {
        this.advertiseUrl = advertiseUrl;
    }

    public boolean isHasAnyTimeCoupons() {
        return hasAnyTimeCoupons;
    }

    public void setHasAnyTimeCoupons(boolean hasAnyTimeCoupons) {
        this.hasAnyTimeCoupons = hasAnyTimeCoupons;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }
}
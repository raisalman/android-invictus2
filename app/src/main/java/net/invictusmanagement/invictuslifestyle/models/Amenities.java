package net.invictusmanagement.invictuslifestyle.models;

public class Amenities extends ModelBase {
    public int amenitiesTypeId;
    public int locationId;
    public String displayName;
    public String description;
    public String maxBookingHours;
    public String colorCode;
    public String advanceBookingDays;
    public String bookingCapacity;
    public Boolean isActive;
    public String listOfAmenitiesType;
    public Boolean deleted;
    public AmenitiesType amenitiesType;

}
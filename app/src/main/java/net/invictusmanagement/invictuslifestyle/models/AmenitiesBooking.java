package net.invictusmanagement.invictuslifestyle.models;

import java.util.Date;

public class AmenitiesBooking extends ModelBase {
    public int amenitiesId;
    public int applicationUserId;
    public int bookingPersonCount;
    public Date bookFrom;
    public Date bookTo;
    public Boolean isApproved;
    public AmenitiesBookingDetails amenities;
    public Boolean deleted;
    public String applicationUser;
    public String description;


}
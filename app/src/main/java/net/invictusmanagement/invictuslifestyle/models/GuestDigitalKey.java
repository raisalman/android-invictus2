package net.invictusmanagement.invictuslifestyle.models;

import java.util.Date;

public class GuestDigitalKey extends ModelBase {
    public Date fromUtc;
    public Date toUtc;
    public String recipient;
    public String email;
    public String notes;
    public String phoneNumber;
    public String key;
    public String mapUrl;
    public String[] mapUrls;
    public boolean isRevoked;
    public String qrCodeSrc;
}
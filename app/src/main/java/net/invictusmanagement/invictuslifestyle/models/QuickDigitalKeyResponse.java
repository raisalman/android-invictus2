package net.invictusmanagement.invictuslifestyle.models;

import java.util.Date;

public class QuickDigitalKeyResponse extends ModelBase {
    public Date fromUtc;
    public Date toUtc;
    public String recipient;
    public String applicationUserId;
    public String notes;
    public String key;
    public boolean deleted;
    public boolean isRevoked;
    public boolean isQuickKey;
    public boolean isPerpetual;
}
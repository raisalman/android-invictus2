package net.invictusmanagement.invictuslifestyle.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Vendors implements Serializable {
    public Date createdUtc;
    public User applicationUser;
    public String name;
    public long id;
    public long locationId;
    public long applicationUserId;
    public ArrayList<Technicians> technicians;
    public boolean deleted;
    public boolean isSelected = false;

    public class Technicians implements Serializable {
        public long vendorMappingId;
        public String technicianName;
        public long id;
        public Date createdUtc;
        public boolean deleted;
        public boolean isSelected = false;
    }
}
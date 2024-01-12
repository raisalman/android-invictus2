package net.invictusmanagement.invictuslifestyle.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddVendor implements Serializable {

    public String name;
    public String email;
    public String phoneNumber;
    public long locationId;
}

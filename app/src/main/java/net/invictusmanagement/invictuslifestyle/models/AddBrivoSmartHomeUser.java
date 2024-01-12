package net.invictusmanagement.invictuslifestyle.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddBrivoSmartHomeUser implements Serializable {

    @SerializedName("BSHUserName")
    public String bshUsername;
    @SerializedName("BSHUserPassword")
    public String bshPassword;
}

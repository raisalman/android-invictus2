package net.invictusmanagement.invictuslifestyle.models;

import java.io.Serializable;

public class ResidentChat extends ModelBase implements Serializable {

    public String displayName;
    public String email;
    public String role;
    //For internal Use
    public boolean isSelected = false;
}
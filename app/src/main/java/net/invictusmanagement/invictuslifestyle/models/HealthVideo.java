package net.invictusmanagement.invictuslifestyle.models;

import java.io.Serializable;

public class HealthVideo extends ModelBase implements Serializable {
    public String title;
    public String description;
    public String healthVideoUrl;
    public String healthVideoThumbnailUrl;
    public boolean isApproved;
    public boolean isFavorite;

}
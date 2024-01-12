package net.invictusmanagement.invictuslifestyle.models;

public class MRSRating {
    public String id;
    public float rating;
    public String review;

    public MRSRating(String id, float rating, String s) {
        this.id = id;
        this.rating = rating;
        this.review = s;
    }
}
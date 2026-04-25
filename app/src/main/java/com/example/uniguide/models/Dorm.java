package com.example.uniguide.models;

public class Dorm {

    private int    id;
    private int    universityId;
    private String dormName;
    private String location;
    private String priceRange;
    private String description;

    public Dorm(int id, int universityId, String dormName,
                String location, String priceRange, String description) {
        this.id           = id;
        this.universityId = universityId;
        this.dormName     = dormName;
        this.location     = location;
        this.priceRange   = priceRange;
        this.description  = description;
    }

    public int    getId()           { return id; }
    public int    getUniversityId() { return universityId; }
    public String getDormName()     { return dormName; }
    public String getLocation()     { return location; }
    public String getPriceRange()   { return priceRange; }
    public String getDescription()  { return description; }
}

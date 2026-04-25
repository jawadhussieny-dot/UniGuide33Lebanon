package com.example.uniguide.models;

public class University {

    private int    id;
    private String name;
    private String city;
    private String website;
    private String description;
    private String founded;
    private String type;
    private String language;

    public University(int id, String name, String city, String website,
                      String description, String founded,
                      String type, String language) {
        this.id          = id;
        this.name        = name;
        this.city        = city;
        this.website     = website;
        this.description = description;
        this.founded     = founded;
        this.type        = type;
        this.language    = language;
    }

    public int    getId()          { return id; }
    public String getName()        { return name; }
    public String getCity()        { return city; }
    public String getWebsite()     { return website; }
    public String getDescription() { return description; }
    public String getFounded()     { return founded; }
    public String getType()        { return type; }
    public String getLanguage()    { return language; }
}

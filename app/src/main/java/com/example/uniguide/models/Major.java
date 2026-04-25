package com.example.uniguide.models;

public class Major {

    private int    id;
    private int    universityId;
    private String majorName;
    private String faculty;
    private int    duration;

    public Major(int id, int universityId, String majorName,
                 String faculty, int duration) {
        this.id           = id;
        this.universityId = universityId;
        this.majorName    = majorName;
        this.faculty      = faculty;
        this.duration     = duration;
    }

    public int    getId()           { return id; }
    public int    getUniversityId() { return universityId; }
    public String getMajorName()    { return majorName; }
    public String getFaculty()      { return faculty; }
    public int    getDuration()     { return duration; }
}

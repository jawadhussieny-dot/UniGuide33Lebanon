package com.example.uniguide.models;

public class StudyPlanCourse {

    private int    id;
    private int    majorId;
    private int    year;
    private int    semester;
    private String courseName;
    private int    credits;

    public StudyPlanCourse(int id, int majorId, int year,
                           int semester, String courseName, int credits) {
        this.id         = id;
        this.majorId    = majorId;
        this.year       = year;
        this.semester   = semester;
        this.courseName = courseName;
        this.credits    = credits;
    }

    public int    getId()         { return id; }
    public int    getMajorId()    { return majorId; }
    public int    getYear()       { return year; }
    public int    getSemester()   { return semester; }
    public String getCourseName() { return courseName; }
    public int    getCredits()    { return credits; }
}

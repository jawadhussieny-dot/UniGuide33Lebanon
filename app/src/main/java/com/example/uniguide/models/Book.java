package com.example.uniguide.models;

public class Book {

    private int    id;
    private int    majorId;
    private String bookName;
    private String author;
    private String edition;

    public Book(int id, int majorId, String bookName,
                String author, String edition) {
        this.id       = id;
        this.majorId  = majorId;
        this.bookName = bookName;
        this.author   = author;
        this.edition  = edition;
    }

    public int    getId()       { return id; }
    public String getBookName() { return bookName; }
    public String getAuthor()   { return author; }
    public String getEdition()  { return edition; }
}

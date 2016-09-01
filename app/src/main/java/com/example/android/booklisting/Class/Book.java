package com.example.android.booklisting.Class;

/**
 * Created by samue_000 on 8/31/2016.
 */
public class Book {

    private String[] author;
    private String title;
    private String subTitle;

    public Book(String[] author, String title, String subTitle) {
        this.author = author;
        this.title = title;
        this.subTitle = subTitle;
    }

    public String[] getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }
}

package com.example.android.booklisting.Class;

/**
 * Created by samue_000 on 8/31/2016.
 */
public class Book {

    private String[] author;
    private String title;
    private String subTitle;
    private String bookImage;
    private boolean hasSubtitle;
    private boolean informationOnly;

    public Book(String[] author, String title, String bookImage) {
        this.author = author;
        this.title = title;
        this.bookImage = bookImage;
        hasSubtitle = false;
        informationOnly = false;
    }

    public Book(String[] author, String title, String subTitle, String bookImage) {
        this.author = author;
        this.title = title;
        this.subTitle = subTitle;
        this.bookImage = bookImage;
        hasSubtitle = true;
        informationOnly = false;
    }

    public Book(String title, String subTitle) {
        this.title = title;
        this.subTitle = subTitle;
        informationOnly = true;
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

    public String getBookURL() {
        return bookImage;
    }

    public boolean getHasSubTitle() {
        return hasSubtitle;
    }

    public boolean getInformationOnly() {
        return informationOnly;
    }
}

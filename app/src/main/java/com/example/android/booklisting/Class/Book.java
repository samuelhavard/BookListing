package com.example.android.booklisting.Class;

/**
 *  The {@link Book} class is a class created to be used in the BookListing Udacity project.
 *  {@link Book} contains information about each book parsed from the GoogleBooks API
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
        hasSubtitle = true;
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

package com.packtpub.felix.bookshelf.inventory.impl.mock;

import com.packtpub.felix.bookshelf.inventory.api.MutableBook;

public class MutableBookImpl implements MutableBook {
    private String isbn;
    private String author;
    private String title;
    private String category;
    private int rating;

    public MutableBookImpl(final String isbn) {
        setIsbn(isbn);
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public int getRating() {
        return rating;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    //TODO: investigate how third party dependencies should be exposed through a bundle
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getCategory()).append(": ");
        buf.append(getTitle()).append(" from ").append(getAuthor());
        buf.append(" [").append(getRating()).append(']');
        return buf.toString();

        // Temporarily commented because can't satisfy guava dependency for the bundle
        /*
        return MoreObjects.toStringHelper(this)
                .add("isbn", isbn)
                .add("author", author)
                .add("title", title)
                .add("category", category)
                .add("rating", rating)
                .toString();
        */
    }
}

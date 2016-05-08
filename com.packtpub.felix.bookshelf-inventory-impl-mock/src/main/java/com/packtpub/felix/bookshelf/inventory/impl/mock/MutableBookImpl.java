package com.packtpub.felix.bookshelf.inventory.impl.mock;

import com.google.common.base.MoreObjects;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("isbn", isbn)
                .add("author", author)
                .add("title", title)
                .add("category", category)
                .add("rating", rating)
                .toString();
    }
}

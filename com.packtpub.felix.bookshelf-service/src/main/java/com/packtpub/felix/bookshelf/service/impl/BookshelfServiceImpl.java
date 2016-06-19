package com.packtpub.felix.bookshelf.service.impl;

import com.packtpub.felix.bookshelf.inventory.api.*;
import com.packtpub.felix.bookshelf.log.api.BookshelfLogHelper;
import com.packtpub.felix.bookshelf.service.api.BookshelfService;
import com.packtpub.felix.bookshelf.service.api.InvalidCredentialsException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BookshelfServiceImpl implements BookshelfService {

    private String sessionId;

    private BookInventory inventory;

    private BookshelfLogHelper logger;

    public BookshelfServiceImpl() {
    }

    public String login(String userName, char[] password) throws InvalidCredentialsException {
        getLogger().debug("Logging in with {0}, {1}", new Object[] { userName, password });
        if ("admin".equals(userName) &&
                Arrays.equals(password, "admin".toCharArray())) {
            this.sessionId = Long.toString(System.currentTimeMillis());
            return this.sessionId;
        }
        throw new InvalidCredentialsException(userName);
    }

    public void logout(String sessionId) {
        getLogger().debug("Logging out with {0}", sessionId);
        checkSession(sessionId);
        this.sessionId = null;
    }

    public boolean sessionIsValid(String sessionId) {
        getLogger().debug("Checking session {0}", sessionId);
        return this.sessionId != null && this.sessionId.equals(sessionId);
    }

    public Book getBook(String sessionId, String isbn) throws BookNotFoundException {
        getLogger().debug("Getting book for {0}, by isbn: {1}", sessionId, isbn);
        checkSession(sessionId);
        return inventory.loadBook(isbn);
    }

    public MutableBook getBookForEdit(String sessionId, String isbn) throws BookNotFoundException {
        getLogger().debug("Getting book for edit for {0}, by isbn: {1}", sessionId, isbn);
        checkSession(sessionId);
        return inventory.loadBookForEdit(isbn);
    }

    public void addBook(String session, String isbn, String title, String author, String category, int rating) throws BookAlreadyExistsException, InvalidBookException {
        getLogger().debug("Adding book for {0}, with isbn {1}, title {2}, author {3}, category {4}, rating {5}",
                session, isbn, title, author, category, rating);
        checkSession(session);

        final MutableBook book = inventory.createBook(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setRating(rating);
        inventory.storeBook(book);
    }

    public void modifyBookCategory(String session, String isbn, String category) throws BookNotFoundException, InvalidBookException {
        getLogger().debug("Modifying book category for {0}, with isbn {1}, category {2}", session, isbn, category);
        checkSession(session);

        final MutableBook book = inventory.loadBookForEdit(isbn);
        book.setCategory(category);
        inventory.storeBook(book);
    }

    public void modifyBookRating(String session, String isbn, int rating) throws BookNotFoundException, InvalidBookException {
        getLogger().debug("Updating book rating for {0}, with isbn {1}, rating {2}", session, isbn, rating);
        checkSession(session);

        final MutableBook book = inventory.loadBookForEdit(isbn);
        book.setRating(rating);
        inventory.storeBook(book);
    }

    public Set<String> getCategories(String session) {
        getLogger().debug("Getting categories for {0}", session);
        checkSession(sessionId);
        return inventory.getCategories();
    }

    public void removeBook(String session, String isbn) throws BookNotFoundException {
        getLogger().debug("Removing book for {0}, with isbn {1}", session, isbn);
        checkSession(session);

        inventory.removeBook(isbn);
    }

    public Set<String> searchBooksByCategory(String session, String categoryLike) {
        getLogger().debug("Searching books by category for {0}, with {1}", session, categoryLike);
        checkSession(session);
        Map<BookInventory.SearchCriteria, String> criteria = new HashMap<BookInventory.SearchCriteria, String>();
        criteria.put(BookInventory.SearchCriteria.CATEGORY_LIKE, categoryLike);
        return inventory.searchBooks(criteria);
    }

    public Set<String> searchBooksByAuthor(String session, String authorLike) {
        getLogger().debug("Searching books by author for {0}, with {1}", session, authorLike);
        checkSession(session);
        Map<BookInventory.SearchCriteria, String> criteria = new HashMap<BookInventory.SearchCriteria, String>();
        criteria.put(BookInventory.SearchCriteria.AUTHOR_LIKE, authorLike);
        return inventory.searchBooks(criteria);
    }

    public Set<String> searchBooksByTitle(String session, String titleLike) {
        getLogger().debug("Searching books by title for {0}, with {1}", session, titleLike);
        checkSession(session);
        Map<BookInventory.SearchCriteria, String> criteria = new HashMap<BookInventory.SearchCriteria, String>();
        criteria.put(BookInventory.SearchCriteria.TITLE_LIKE, titleLike);
        return inventory.searchBooks(criteria);
    }

    public Set<String> searchBooksByRating(String session, int ratingLower, int ratingUpper) {
        getLogger().debug("Searching books by rating for {0}, from {1} to {2}", session, ratingLower, ratingUpper);
        checkSession(session);
        Map<BookInventory.SearchCriteria, String> criteria = new HashMap<BookInventory.SearchCriteria, String>();
        criteria.put(BookInventory.SearchCriteria.GRADE_GT, Integer.toString(ratingLower));
        criteria.put(BookInventory.SearchCriteria.GRADE_LT, Integer.toString(ratingUpper));
        return inventory.searchBooks(criteria);
    }

    protected void checkSession(String sessionId) {
        getLogger().debug("Checking session {0}", sessionId);
        if (!sessionIsValid(sessionId)) {
            throw new SessionNotValidRuntimeException(sessionId);
        }
    }

    private BookshelfLogHelper getLogger() {
        return logger;
    }
}

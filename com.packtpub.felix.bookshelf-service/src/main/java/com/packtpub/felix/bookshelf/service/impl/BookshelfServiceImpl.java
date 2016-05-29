package com.packtpub.felix.bookshelf.service.impl;

import com.packtpub.felix.bookshelf.inventory.api.*;
import com.packtpub.felix.bookshelf.service.api.BookshelfService;
import com.packtpub.felix.bookshelf.service.api.InvalidCredentialsException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BookshelfServiceImpl implements BookshelfService {

    private String sessionId;

    private final BundleContext context;
    private final BookInventory inventory;

    public BookshelfServiceImpl(final BundleContext context) {
        this.context = context;
        this.inventory = lookupBookInventory();
    }

    public String login(String userName, char[] password) throws InvalidCredentialsException {
        if ("admin".equals(userName) &&
                Arrays.equals(password, "admin".toCharArray())) {
            this.sessionId = Long.toString(System.currentTimeMillis());
            return this.sessionId;
        }
        throw new InvalidCredentialsException(userName);
    }

    public void logout(String sessionId) {
        checkSession(sessionId);
        this.sessionId = null;
    }

    public boolean sessionIsValid(String sessionId) {
        return this.sessionId != null && this.sessionId.equals(sessionId);
    }

    public Book getBook(String sessionId, String isbn) throws BookNotFoundException {
        checkSession(sessionId);
        return inventory.loadBook(isbn);
    }

    public MutableBook getBookForEdit(String sessionId, String isbn) throws BookNotFoundException {
        checkSession(sessionId);
        return inventory.loadBookForEdit(isbn);
    }

    public void addBook(String session, String isbn, String title, String author, String category, int rating) throws BookAlreadyExistsException, InvalidBookException {
        checkSession(session);

        final MutableBook book = inventory.createBook(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setRating(rating);
        inventory.storeBook(book);
    }

    public void modifyBookCategory(String session, String isbn, String category) throws BookNotFoundException, InvalidBookException {
        checkSession(session);

        final MutableBook book = inventory.loadBookForEdit(isbn);
        book.setCategory(category);
        inventory.storeBook(book);
    }

    public void modifyBookRating(String session, String isbn, int rating) throws BookNotFoundException, InvalidBookException {
        checkSession(session);

        final MutableBook book = inventory.loadBookForEdit(isbn);
        book.setRating(rating);
        inventory.storeBook(book);
    }

    public Set<String> getCategories(String session) {
        checkSession(sessionId);
        return inventory.getCategories();
    }

    public void removeBook(String session, String isbn) throws BookNotFoundException {
        checkSession(session);

        inventory.removeBook(isbn);
    }

    public Set<String> searchBooksByCategory(String session, String categoryLike) {
        checkSession(session);
        Map<BookInventory.SearchCriteria, String> criteria = new HashMap<BookInventory.SearchCriteria, String>();
        criteria.put(BookInventory.SearchCriteria.CATEGORY_LIKE, categoryLike);
        return inventory.searchBooks(criteria);
    }

    public Set<String> searchBooksByAuthor(String session, String authorLike) {
        checkSession(session);
        Map<BookInventory.SearchCriteria, String> criteria = new HashMap<BookInventory.SearchCriteria, String>();
        criteria.put(BookInventory.SearchCriteria.AUTHOR_LIKE, authorLike);
        return inventory.searchBooks(criteria);
    }

    public Set<String> searchBooksByTitle(String session, String titleLike) {
        checkSession(session);
        Map<BookInventory.SearchCriteria, String> criteria = new HashMap<BookInventory.SearchCriteria, String>();
        criteria.put(BookInventory.SearchCriteria.TITLE_LIKE, titleLike);
        return inventory.searchBooks(criteria);
    }

    public Set<String> searchBooksByRating(String session, int ratingLower, int ratingUpper) {
        checkSession(session);
        Map<BookInventory.SearchCriteria, String> criteria = new HashMap<BookInventory.SearchCriteria, String>();
        criteria.put(BookInventory.SearchCriteria.GRADE_GT, Integer.toString(ratingLower));
        criteria.put(BookInventory.SearchCriteria.GRADE_LT, Integer.toString(ratingUpper));
        return inventory.searchBooks(criteria);
    }

    private BookInventory lookupBookInventory() {
        String name = BookInventory.class.getName();
        ServiceReference ref = this.context.getServiceReference(name);
        if (ref == null) {
            throw new BookInventoryNotRegisteredRuntimeException(name);
        }
        return (BookInventory) this.context.getService(ref);
    }

    protected void checkSession(String sessionId) {
        if (!sessionIsValid(sessionId)) {
            throw new SessionNotValidRuntimeException(sessionId);
        }
    }
}

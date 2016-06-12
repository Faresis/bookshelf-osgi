package com.packtpub.felix.bookshelf.service.tui;

import com.packtpub.felix.bookshelf.inventory.api.Book;
import com.packtpub.felix.bookshelf.inventory.api.BookAlreadyExistsException;
import com.packtpub.felix.bookshelf.inventory.api.BookNotFoundException;
import com.packtpub.felix.bookshelf.inventory.api.InvalidBookException;
import com.packtpub.felix.bookshelf.service.api.BookshelfService;
import com.packtpub.felix.bookshelf.service.api.InvalidCredentialsException;
import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.service.command.Descriptor;

import java.util.HashSet;
import java.util.Set;

@Component(name = "BookshelfServiceProxy")
@Provides
public class BookshelfServiceProxyImpl implements BookshelfServiceProxy {

    public static final String SCOPE = "book";
    public static final String FUNCTIONS_STR = "[add,search]";

    @ServiceProperty(name = "osgi.command.scope", value = SCOPE)
    String gogoScope;

    @ServiceProperty(name = "osgi.command.function", value = FUNCTIONS_STR)
    String[] gogoFunctions;


    @Requires
    private BookshelfService service;

    public BookshelfServiceProxyImpl() {
    }

    @Descriptor("Add a new book")
    public String add(
            @Descriptor("username") String username,
            @Descriptor("password") String password,
            @Descriptor("ISBN") String isbn,
            @Descriptor("Title") String title,
            @Descriptor("Author") String author,
            @Descriptor("Category") String category,
            @Descriptor("Rating (0..10)") int rating) throws InvalidCredentialsException, InvalidBookException, BookAlreadyExistsException {
        String sessionId = service.login(username, password.toCharArray());
        service.addBook(sessionId, isbn, title, author, category, rating);
        return isbn;
    }

    @Descriptor("Search books by rating")
    public Set<Book> search(
            @Descriptor("username") String username,
            @Descriptor("password") String password,
            @Descriptor("search on attribute: rating") String attribute,
            @Descriptor("lower rating limit (inclusive)") int lower,
            @Descriptor("upper rating limit (inclusive)") int upper)
            throws InvalidCredentialsException {
        if (!"rating".equals(attribute)) {
            throw new RuntimeException("Invalid attribute. Expected 'rating' got " + attribute);
        }
        String sessionId = service.login(username, password.toCharArray());
        Set<String> result = service.searchBooksByRating(sessionId, lower, upper);
        return getBooks(sessionId, service, result);
    }

    @Descriptor("Search books by author, title or category")
    public Set<Book> search(@Descriptor("username") String username,
                            @Descriptor("password") String password,
                            @Descriptor("Search on attribute: author, title or category") String attribute,
                            @Descriptor("match like (use % at the beginning or end of <like> for wildcard") String filter)
            throws InvalidCredentialsException {

        String sessionId = service.login(username, password.toCharArray());

        Set<String> results;

        if ("title".equals(attribute)) {
            results = service.searchBooksByTitle(sessionId, filter);
        } else if ("author".equals(attribute)) {
            results = service.searchBooksByAuthor(sessionId, filter);
        } else if ("category".equals(attribute)) {
            results = service.searchBooksByCategory(sessionId, filter);
        } else {
            throw new RuntimeException("Invalid attribute. Expecting one of { 'author', 'title', 'category' } got " + attribute);
        }

        return getBooks(sessionId, service, results);
    }

    private static Set<Book> getBooks(String sessionId, BookshelfService service, Set<String> isbns) {
        Set<Book> books = new HashSet<Book>();
        for (String isbn : isbns) {
            try {
                books.add(service.getBook(sessionId, isbn));
            } catch (BookNotFoundException e) {
                System.out.println("ISBN " + isbn + "referenced but not found.");
            }
        }
        return books;
    }
}

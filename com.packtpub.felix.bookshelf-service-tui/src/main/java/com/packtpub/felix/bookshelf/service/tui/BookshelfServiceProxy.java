package com.packtpub.felix.bookshelf.service.tui;

import com.packtpub.felix.bookshelf.inventory.api.Book;
import com.packtpub.felix.bookshelf.inventory.api.BookNotFoundException;
import com.packtpub.felix.bookshelf.service.api.BookshelfService;
import com.packtpub.felix.bookshelf.service.api.InvalidCredentialsException;
import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.HashSet;
import java.util.Set;

public class BookshelfServiceProxy {

    public static final String SCOPE = "book";

    public static final String[] FUNCTIONS = new String[] { "search" };

    private BundleContext bundleContext;

    public BookshelfServiceProxy(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
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
        BookshelfService service = lookupService();
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
        BookshelfService service = lookupService();

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

    protected BookshelfService lookupService() {
        ServiceReference reference = bundleContext.getServiceReference(BookshelfService.class.getName());
        if (reference == null) {
            throw new RuntimeException("Bookshelf service not registered.");
        }
        BookshelfService service = (BookshelfService) this.bundleContext.getService(reference);
        if (service == null) {
            throw new RuntimeException("Bookshelf service not registered.");
        }
        return service;
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

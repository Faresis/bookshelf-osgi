package com.packtpub.felix.bookshelf.service.impl.activator;

import com.packtpub.felix.bookshelf.inventory.api.BookAlreadyExistsException;
import com.packtpub.felix.bookshelf.inventory.api.BookNotFoundException;
import com.packtpub.felix.bookshelf.inventory.api.InvalidBookException;
import com.packtpub.felix.bookshelf.service.api.BookshelfService;
import com.packtpub.felix.bookshelf.service.api.InvalidCredentialsException;
import com.packtpub.felix.bookshelf.service.impl.BookshelfServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.util.Set;

public class BookshelfServiceImplActivator implements BundleActivator {

    private ServiceRegistration reg = null;

    public void start(BundleContext bundleContext) throws Exception {
        this.reg = bundleContext.registerService(BookshelfService.class.getName(),
                new BookshelfServiceImpl(bundleContext), null);
        testService(bundleContext);
    }

    public void stop(BundleContext bundleContext) throws Exception {
        if (this.reg != null) {
            bundleContext.ungetService(reg.getReference());
        }
    }

    private static void testService(BundleContext bundleContext) {
        String name = BookshelfService.class.getName();
        ServiceReference ref = bundleContext.getServiceReference(name);
        if (ref == null) {
            throw new RuntimeException("Service is not registered: " + name);
        }
        BookshelfService service = (BookshelfService) bundleContext.getService(ref);

        String sessionId;
        try {
            System.out.println("\nSigning in...");
            sessionId = service.login("admin", "admin".toCharArray());
        } catch (InvalidCredentialsException ex) {
            ex.printStackTrace();
            return;
        }

        try {
            System.out.println("\nAdding books...");
            service.addBook(sessionId, "123-4567890100", "Book 1 Title", "John Doe", "Group 1", 0);
            service.addBook(sessionId, "123-4567890101", "Book 2 Title", "Will Smith", "Group 1", 0);
            service.addBook(sessionId, "123-4567890200", "Book 3 Title", "John Doe", "Group 2", 0);
            service.addBook(sessionId, "123-4567890201", "Book 4 Title", "John Doe", "Group 2", 0);
        } catch (BookAlreadyExistsException ex) {
            ex.printStackTrace();
            return;
        } catch (InvalidBookException ex) {
            ex.printStackTrace();
            return;
        }

        String authorLike = "%Doe";
        System.out.println("Searching for books with author like " + authorLike);
        Set<String> results = service.searchBooksByAuthor(sessionId, authorLike);
        for (String isbn : results) {
            try {
                System.out.println(" - " + service.getBook(sessionId, isbn));
            } catch (BookNotFoundException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}

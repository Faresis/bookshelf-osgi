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
    }

    public void stop(BundleContext bundleContext) throws Exception {
        if (this.reg != null) {
            bundleContext.ungetService(reg.getReference());
        }
    }
}

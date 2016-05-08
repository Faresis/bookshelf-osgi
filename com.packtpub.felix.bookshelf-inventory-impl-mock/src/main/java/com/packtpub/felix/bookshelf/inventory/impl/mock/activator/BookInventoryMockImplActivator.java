package com.packtpub.felix.bookshelf.inventory.impl.mock.activator;

import com.packtpub.felix.bookshelf.inventory.api.BookInventory;
import com.packtpub.felix.bookshelf.inventory.impl.mock.BookInventoryMockImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class BookInventoryMockImplActivator implements BundleActivator {

    private ServiceRegistration reg;

    public void start(BundleContext bundleContext) throws Exception {
        System.out.println("\nStarting Book Inventory Mock Impl.");
        this.reg = bundleContext.registerService(BookInventory.class.getName(), new BookInventoryMockImpl(), null);
    }

    public void stop(BundleContext bundleContext) throws Exception {
        System.out.println("\nStopping Book Inventory Mock Impl.");
        if (this.reg != null) {
            bundleContext.ungetService(reg.getReference());
            this.reg = null;
        }
    }
}

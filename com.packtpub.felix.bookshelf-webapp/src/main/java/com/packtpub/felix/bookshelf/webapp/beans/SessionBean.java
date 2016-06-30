package com.packtpub.felix.bookshelf.webapp.beans;

import com.packtpub.felix.bookshelf.service.api.BookshelfService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import javax.servlet.ServletContext;

public class SessionBean {
    private static final String OSGI_BUNDLECONTEXT = "osgi-bundlecontext";

    private BundleContext ctx;

    private String sessionId;

    public void initialize(ServletContext context) {
        this.ctx = (BundleContext) context.getAttribute(OSGI_BUNDLECONTEXT);
    }

    public BookshelfService getBookshelf() {
        ServiceReference ref = ctx.getServiceReference(BookshelfService.class.getName());
        BookshelfService service = (BookshelfService) ctx.getService(ref);
        return service;
    }

    public boolean isSessionValid() {
        return getBookshelf().sessionIsValid(getSessionId());
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

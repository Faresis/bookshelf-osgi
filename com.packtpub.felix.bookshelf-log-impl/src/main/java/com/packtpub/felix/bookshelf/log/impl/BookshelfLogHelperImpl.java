package com.packtpub.felix.bookshelf.log.impl;

import com.packtpub.felix.bookshelf.log.api.BookshelfLogHelper;
import org.osgi.service.log.LogService;

import java.text.MessageFormat;

public class BookshelfLogHelperImpl implements BookshelfLogHelper {

    private LogService log;

    public void debug(String pattern, Object... args) {
        String message = MessageFormat.format(pattern, args);
        this.log.log(LogService.LOG_DEBUG, message);
    }

    public void debug(String pattern, Throwable throwable, Object... args) {
        String message = MessageFormat.format(pattern, args) + throwable.toString();
        this.log.log(LogService.LOG_DEBUG, message);
    }

    public void info(String pattern, Object... args) {
        String message = MessageFormat.format(pattern, args);
        this.log.log(LogService.LOG_INFO, message);
    }

    public void warn(String pattern, Object... args) {
        String message = MessageFormat.format(pattern, args);
        this.log.log(LogService.LOG_WARNING, message);
    }

    public void warn(String pattern, Throwable throwable, Object... args) {
        String message = MessageFormat.format(pattern, args) + throwable.toString();
        this.log.log(LogService.LOG_WARNING, message);
    }

    public void error(String pattern, Object... args) {
        String message = MessageFormat.format(pattern, args);
        this.log.log(LogService.LOG_ERROR, message);
    }

    public void error(String pattern, Throwable throwable, Object... args) {
        String message = MessageFormat.format(pattern, args) + throwable.toString();
        this.log.log(LogService.LOG_ERROR, message);
    }
}

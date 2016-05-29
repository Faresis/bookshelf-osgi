package com.packtpub.felix.bookshelf.service.api;

public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException(final String user) {
        super("Wrong credentials for " + user);
    }
}

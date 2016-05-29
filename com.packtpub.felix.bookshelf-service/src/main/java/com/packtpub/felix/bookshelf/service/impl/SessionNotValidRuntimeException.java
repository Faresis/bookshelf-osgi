package com.packtpub.felix.bookshelf.service.impl;

public class SessionNotValidRuntimeException extends RuntimeException {
    public SessionNotValidRuntimeException(String sessionId) {
        super("Session { " + sessionId + " } is not valid.");
    }
}

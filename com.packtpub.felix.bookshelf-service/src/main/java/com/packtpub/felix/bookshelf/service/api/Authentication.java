package com.packtpub.felix.bookshelf.service.api;

public interface Authentication {
    String login(String userName, char[] password) throws InvalidCredentialsException;
    void logout(String session);
    boolean sessionIsValid(String session);
}

package com.packtpub.felix.bookshelf.service.impl;

public class BookInventoryNotRegisteredRuntimeException extends RuntimeException {
    public BookInventoryNotRegisteredRuntimeException(String name) {
        super("Unable to find BookInventory service by name " + name);
    }
}

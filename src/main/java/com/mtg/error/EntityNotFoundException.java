package com.mtg.error;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Long id, String className) {
        super("Could not find " + className + " with id = " + id);
    }

    public EntityNotFoundException(String name, String className) {
        super("Could not find " + className + " with name = " + name);
    }

    public EntityNotFoundException(String username, String name, String className) {
        super("Could not find " + className + " owned by user " + username + " with name = " + name);
    }

    public EntityNotFoundException(String username, Long id, String className) {
        super("Could not find " + className + " owned by user " + username + " with id = " + id);
    }

}
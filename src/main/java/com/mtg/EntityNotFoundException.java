package com.mtg;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Long id, String type) {
        super("Could not find " + type + " " + id);
    }

    public EntityNotFoundException(String name, String type) {
        super("Could not find " + type + " " + name);
    }

}
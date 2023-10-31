package com.mtg.error;

public class RecordAlreadyExistsException extends RuntimeException {

    public RecordAlreadyExistsException(String className, String field) {
        super("A " + className + " with this " + field + " already exists.");
    }

}

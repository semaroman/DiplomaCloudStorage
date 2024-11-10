package ru.netology.diplomaCloudStorage.exception;

public class DuplicateFileNameException extends RuntimeException {
    public DuplicateFileNameException(String message) {
        super(message);
    }
}

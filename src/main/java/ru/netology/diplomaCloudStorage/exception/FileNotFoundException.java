package ru.netology.diplomaCloudStorage.exception;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(String message) {
        super(message);
    }
}

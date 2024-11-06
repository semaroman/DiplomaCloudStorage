package ru.netology.diplomaCloudStorage.exception;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException() {
        super("Пользователь не авторизован");
    }
}
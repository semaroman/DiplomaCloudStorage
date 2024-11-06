package ru.netology.diplomaCloudStorage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.diplomaCloudStorage.dto.ResponseError;
import ru.netology.diplomaCloudStorage.exception.AuthorizationException;
import ru.netology.diplomaCloudStorage.exception.BadCredentialsException;

import java.io.IOException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {BindException.class, BadCredentialsException.class, IOException.class})
    ResponseError handleBindException(Exception e) {
        logger.error(e.getMessage());
        return new ResponseError(e.getMessage(), -1);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthorizationException.class)
    ResponseError handleAuthorizationException(AuthorizationException e) {
        logger.error(e.getMessage());
        return new ResponseError(e.getMessage(), -1);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    ResponseError handleRuntimeException(RuntimeException e) {
        logger.error(e.getMessage());
        return new ResponseError(e.getMessage(), -1);
    }
}
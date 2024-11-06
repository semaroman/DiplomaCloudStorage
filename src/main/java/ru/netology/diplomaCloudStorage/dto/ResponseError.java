package ru.netology.diplomaCloudStorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseError {
    private final String message;
    private final int id;
}
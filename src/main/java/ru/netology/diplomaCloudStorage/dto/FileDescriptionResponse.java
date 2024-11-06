package ru.netology.diplomaCloudStorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDescriptionResponse {
    private final String filename;
    private final int size;
}
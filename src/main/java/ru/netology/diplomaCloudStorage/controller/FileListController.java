package ru.netology.diplomaCloudStorage.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.diplomaCloudStorage.dto.FileDescriptionResponse;
import ru.netology.diplomaCloudStorage.service.AuthorizationService;
import ru.netology.diplomaCloudStorage.service.FileService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/list")
@Validated
public class FileListController {
    private final FileService fileService;
    private final AuthorizationService authorizationService;

    public FileListController(FileService fileService, AuthorizationService authorizationService) {
        this.fileService = fileService;
        this.authorizationService = authorizationService;
    }

    @GetMapping
    public List<FileDescriptionResponse> getFileList(@RequestHeader("auth-token")
                                                       @NotBlank String authToken, @Min(1) int limit) {
        authorizationService.checkToken(authToken);
        return fileService.getFileList(limit);
    }
}
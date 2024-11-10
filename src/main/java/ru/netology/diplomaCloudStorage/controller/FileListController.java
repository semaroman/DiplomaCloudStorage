package ru.netology.diplomaCloudStorage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.diplomaCloudStorage.dto.FileDescriptionResponse;
import ru.netology.diplomaCloudStorage.entity.FileEntity;
import ru.netology.diplomaCloudStorage.entity.UserEntity;
import ru.netology.diplomaCloudStorage.service.AuthorizationService;
import ru.netology.diplomaCloudStorage.service.FileService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/list")
@Validated
public class FileListController {
    private final FileService fileService;
    private final AuthorizationService authorizationService;

    public FileListController(FileService fileService, AuthorizationService authenticationService) {
        this.fileService = fileService;
        this.authorizationService = authenticationService;
    }

    @GetMapping
    public List<FileDescriptionResponse> getFileList(@RequestHeader("auth-token") @NotBlank String authToken,
                                                     @Min(1) int limit) {

        log.info("Downloading files with limit {}", limit);
        UserEntity user = authorizationService.checkToken(authToken);
        List<FileEntity> filesList = fileService.getAllFiles(user, limit);
        log.info("User {} downloaded files list with limit {}", user.getLogin(), limit);
        return filesList.stream()
                .map(file -> new FileDescriptionResponse(file.getName(), file.getContent().length))
                .collect(Collectors.toList());
    }
}

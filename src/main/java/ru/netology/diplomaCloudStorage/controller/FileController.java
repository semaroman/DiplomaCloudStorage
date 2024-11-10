package ru.netology.diplomaCloudStorage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplomaCloudStorage.dto.FileNameRequest;
import ru.netology.diplomaCloudStorage.entity.UserEntity;
import ru.netology.diplomaCloudStorage.repository.TokenRepository;
import ru.netology.diplomaCloudStorage.repository.UserRepository;
import ru.netology.diplomaCloudStorage.service.AuthorizationService;
import ru.netology.diplomaCloudStorage.service.FileService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/file")
@Validated
public class FileController {
    private final AuthorizationService authorizationService;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public FileController(AuthorizationService authorizationService, FileService fileService,
                          UserRepository userRepository, TokenRepository tokenRepository) {
        this.authorizationService = authorizationService;
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") @NotBlank String authToken,
                                        @RequestParam("filename") @NotBlank String filename,
                                        @RequestBody @NotNull MultipartFile file) throws IOException {
        log.info("Uploading");
        UserEntity user = authorizationService.checkToken(authToken);
        fileService.addFile(filename, file.getBytes(), user);
        log.info("User {} uploaded file {}", user.getLogin(), filename);
        return new ResponseEntity("Uploaded successfully", HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadFile(@RequestHeader("auth-token") @NotBlank String authToken,
                               @NotBlank String filename) {
        log.info("Downloading");
        UserEntity user = authorizationService.checkToken(authToken);
        log.info("User {} downloaded file {}", user.getLogin(), filename);
        return fileService.downloadFile(filename, user);
    }

    @PutMapping
    public ResponseEntity<?> renameFile(@RequestHeader("auth-token") @NotBlank String authToken,
                                        @RequestParam("filename") @NotBlank String filename,
                                        @Valid @RequestBody FileNameRequest newFilename) {
        log.info("Renaming");
        UserEntity user = authorizationService.checkToken(authToken);
        String newName = newFilename.getFilename();
        fileService.renameFile(filename, user, newName);
        log.info("User {} renamed file {} to {}",
                user.getLogin(), filename, newName);
        return new ResponseEntity("Renamed successfully", HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") @NotBlank String authToken,
                                        @RequestParam("filename") @NotBlank String filename) {
        log.info("Deleting");
        UserEntity user = authorizationService.checkToken(authToken);
        fileService.deleteFile(filename, user);
        log.info("User {} deleted file {}", user.getLogin(), filename);
        return new ResponseEntity("Deleted successfully", HttpStatus.OK);
    }
}

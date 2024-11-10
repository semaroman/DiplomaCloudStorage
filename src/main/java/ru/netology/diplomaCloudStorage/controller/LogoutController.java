package ru.netology.diplomaCloudStorage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.diplomaCloudStorage.service.AuthorizationService;

import javax.validation.constraints.NotBlank;

@Slf4j
@RestController
@RequestMapping("/logout")
@Validated
public class LogoutController {
    private final AuthorizationService authorizationService;

    public LogoutController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public ResponseEntity<?> logout(@RequestHeader("auth-token") @NotBlank String authToken) {
        log.info("Logging out user with auth-token {}", authToken);
        authorizationService.logout(authToken);
        log.info("User {} logged out", authToken);
        return new ResponseEntity("Logged out successfully", HttpStatus.OK);
    }
}

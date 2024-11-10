package ru.netology.diplomaCloudStorage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.diplomaCloudStorage.dto.LoginResponse;
import ru.netology.diplomaCloudStorage.dto.LoginRequest;
import ru.netology.diplomaCloudStorage.entity.UserEntity;
import ru.netology.diplomaCloudStorage.service.AuthorizationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {
    private final AuthorizationService authorizationService;

    public LoginController(AuthorizationService authenticationService) {
        this.authorizationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        final Long id = 1L;
        final String login = loginRequest.getLogin();
        final String password = loginRequest.getPassword();
        UserEntity user = new UserEntity(id, login, password);

        log.info("Logging in user {}", login);
        final String token = String.valueOf(authorizationService.login(user));
        final LoginResponse loginResponse = new LoginResponse(token);
        log.info("User {} logged in with auth-token {}", login, token);

        return ResponseEntity.ok(loginResponse);
    }
}

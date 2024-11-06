package ru.netology.diplomaCloudStorage.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.diplomaCloudStorage.dto.LoginRequest;
import ru.netology.diplomaCloudStorage.dto.LoginResponse;
import ru.netology.diplomaCloudStorage.service.AuthorizationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/login")
public class LoginController {
    private final AuthorizationService authorizationService;

    public LoginController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public LoginResponse login(@Valid @RequestBody LoginRequest loginInRequest) {
        return authorizationService.login(loginInRequest);
    }
}
package ru.netology.diplomaCloudStorage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.diplomaCloudStorage.dto.LoginRequest;
import ru.netology.diplomaCloudStorage.entity.UserEntity;
import ru.netology.diplomaCloudStorage.repository.TokenRepository;
import ru.netology.diplomaCloudStorage.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;

class AuthorizationServiceTest {
    public static final String TOKEN = "semaroman";
    public static final String UNKNOWN_TOKEN = "alexivanov";
    public static final String USER = "semashkevich";
    public static final String UNKNOWN_USER = "ivanov";
    public static final String CORRECT_PASSWORD = "qwerty123";

    private final UserRepository userRepository = createUserRepositoryMock();
    private final TokenRepository tokenRepository = createTokenRepositoryMock();

    private UserRepository createUserRepositoryMock() {
        final UserRepository userRepository = Mockito.mock(UserRepository.class);
        when(userRepository.findById(USER)).thenReturn(Optional.of(new UserEntity(USER, CORRECT_PASSWORD)));
        when(userRepository.findById(UNKNOWN_USER)).thenReturn(Optional.empty());
        return userRepository;
    }

    private TokenRepository createTokenRepositoryMock() {
        final TokenRepository tokenRepository = Mockito.mock(TokenRepository.class);
        when(tokenRepository.existsById(TOKEN.split(" ")[1].trim())).thenReturn(true);
        when(tokenRepository.existsById(UNKNOWN_TOKEN)).thenReturn(false);
        return tokenRepository;
    }

    @Test
    void login() {
        final AuthorizationService authorizationService = new AuthorizationService(userRepository, tokenRepository);
        Assertions.assertDoesNotThrow(() -> authorizationService.login(new LoginRequest(USER, CORRECT_PASSWORD)));
    }

    @Test
    void login_userNotFound() {
        final AuthorizationService authorizationService = new AuthorizationService(userRepository, tokenRepository);
        Assertions.assertThrows(RuntimeException.class, () -> authorizationService.login(new LoginRequest(UNKNOWN_USER, CORRECT_PASSWORD)));
    }

    @Test
    void login_incorrectPassword() {
        final AuthorizationService authorizationService = new AuthorizationService(userRepository, tokenRepository);
        Assertions.assertThrows(RuntimeException.class, () -> authorizationService.login(new LoginRequest(USER, "abcdef")));
    }

    @Test
    void logout() {
        final AuthorizationService authorizationService = new AuthorizationService(userRepository, tokenRepository);
        Assertions.assertDoesNotThrow(() -> authorizationService.logout(TOKEN));
    }

    @Test
    void checkToken() {
        final AuthorizationService authorizationService = new AuthorizationService(userRepository, tokenRepository);
        Assertions.assertDoesNotThrow(() -> authorizationService.checkToken(TOKEN));
    }

    @Test
    void checkToken_failed() {
        final AuthorizationService authorizationService = new AuthorizationService(userRepository, tokenRepository);
        Assertions.assertThrows(RuntimeException.class, () -> authorizationService.checkToken(UNKNOWN_TOKEN));
    }
}
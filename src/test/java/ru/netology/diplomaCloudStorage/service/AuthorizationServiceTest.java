package ru.netology.diplomaCloudStorage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.diplomaCloudStorage.entity.UserEntity;
import ru.netology.diplomaCloudStorage.repository.TokenRepository;
import ru.netology.diplomaCloudStorage.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class AuthorizationServiceTest {
    public static final String TOKEN = "semaroman";
    public static final String UNKNOWN_TOKEN = "alexivanov";
    public static final Long ID = 1L;
    public static final String LOGIN = "semashkevich";
    public static final String UNKNOWN_LOGIN = "ivanov";
    public static final String PASSWORD = "qwerty12345";
    public static final String INCORRECT_PASSWORD = "password9876";

    private final UserRepository userRepository = createUserRepositoryMock();
    private final TokenRepository tokenRepository = createTokenRepositoryMock();

    private UserRepository createUserRepositoryMock() {
        final UserRepository userRepository = Mockito.mock(UserRepository.class);

        when(userRepository.findUserByLogin(LOGIN)).
                thenReturn(Optional.of(new UserEntity(1L, LOGIN, PASSWORD)));
        when(userRepository.findUserByLogin(UNKNOWN_LOGIN))
                .thenReturn(Optional.empty());
        return userRepository;
    }

    private TokenRepository createTokenRepositoryMock() {
        final TokenRepository tokenRepository = Mockito.mock(TokenRepository.class);

        when(tokenRepository.getLoginByToken(TOKEN.split(" ")[1].trim()))
                .thenReturn(Optional.of(LOGIN));
        when(tokenRepository.getLoginByToken(UNKNOWN_TOKEN))
                .thenReturn(Optional.empty());
        return tokenRepository;
    }

    @Test
    void testLogin() {
        final AuthorizationService authorizationService =
                new AuthorizationService(userRepository, tokenRepository);
        Assertions.assertDoesNotThrow(() -> authorizationService
                .login(new UserEntity(ID, LOGIN, PASSWORD)));
    }

    @Test
    void testUnknownLogin() {
        final AuthorizationService authorizationService =
                new AuthorizationService(userRepository, tokenRepository);
        Assertions.assertThrows(RuntimeException.class, () -> authorizationService
                .login(new UserEntity(ID, UNKNOWN_LOGIN, PASSWORD)));
    }

    @Test
    void testIncorrectPassword() {
        final AuthorizationService authorizationService =
                new AuthorizationService(userRepository, tokenRepository);
        Assertions.assertThrows(RuntimeException.class, () -> authorizationService
                .login(new UserEntity(ID, LOGIN, INCORRECT_PASSWORD)));
    }

    @Test
    void testLogout() {
        final AuthorizationService authenticationService =
                new AuthorizationService(userRepository, tokenRepository);
        Assertions.assertDoesNotThrow(() -> authenticationService
                .logout(TOKEN));
    }

    @Test
    void testToken() {
        final AuthorizationService authorizationService =
                new AuthorizationService(userRepository, tokenRepository);
        Assertions.assertDoesNotThrow(() -> authorizationService
                .checkToken(TOKEN));
    }

    @Test
    void testUnknownToken() {
        final AuthorizationService authorizationService =
                new AuthorizationService(userRepository, tokenRepository);
        Assertions.assertThrows(RuntimeException.class, () -> authorizationService
                .checkToken(UNKNOWN_TOKEN));
    }
}

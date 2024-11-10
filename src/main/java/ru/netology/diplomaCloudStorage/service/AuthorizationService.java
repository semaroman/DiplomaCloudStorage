package ru.netology.diplomaCloudStorage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.diplomaCloudStorage.entity.UserEntity;
import ru.netology.diplomaCloudStorage.exception.AuthorizationException;
import ru.netology.diplomaCloudStorage.exception.BadCredentialsException;
import ru.netology.diplomaCloudStorage.repository.TokenRepository;
import ru.netology.diplomaCloudStorage.repository.UserRepository;

import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class AuthorizationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final Random random = new Random();

    public AuthorizationService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public String login(UserEntity user) {
        final String login = user.getLogin();
        final String password = user.getPassword();

        log.info("Finding user {} in database", login);

        final Optional<UserEntity> repositoryUser = userRepository.findUserByLogin(login);
        if (repositoryUser.isEmpty()) {
            log.info("User {} is not found", login);
            throw new BadCredentialsException("Пользователь " + login + " не найден");
        }
        log.info("User {} is found", login);

        if (!repositoryUser.get().getPassword().equals(password)) {
            log.info("User {} inputted incorrect password", login);
            throw new BadCredentialsException("Неверный пароль");
        }

        final String token = String.valueOf(random.nextLong());
        tokenRepository.putTokenAndLogin(token, login);

        return token;
    }

    public void logout(String token) {
        tokenRepository.removeTokenAndLoginByToken(token);
        log.info("Token {} was removed from hash map", token);
    }

    public UserEntity checkToken(String token) {
        String tokenWithoutBearer;
        String[] tokenParts = token.split(" ");
        if (tokenParts.length >= 2) {
            tokenWithoutBearer = tokenParts[1];
        } else {
            tokenWithoutBearer = token;
        }

        log.info("Token {}, token without bearer {}", token, tokenWithoutBearer);
        log.info("Finding token {} in hash map", tokenWithoutBearer);

        final Optional<String> login = tokenRepository.getLoginByToken(tokenWithoutBearer);
        if (login.isEmpty()) {
            log.info("Token {} is not found", tokenWithoutBearer);
            throw new AuthorizationException("Пользователь не авторизован");
        }

        log.info("Token {} is found", tokenWithoutBearer);
        log.info("User {}", login.get());

        final Optional<UserEntity> user = userRepository.findUserByLogin(login.get());
        if (user.isEmpty()) {
            log.info("User {} is not found", login.get());
            throw new AuthorizationException("Пользователь не найден");
        }

        return user.get();
    }
}

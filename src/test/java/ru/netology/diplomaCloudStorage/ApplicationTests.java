package ru.netology.diplomaCloudStorage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.netology.diplomaCloudStorage.dto.FileNameRequest;
import ru.netology.diplomaCloudStorage.dto.LoginRequest;
import ru.netology.diplomaCloudStorage.entity.FileEntity;
import ru.netology.diplomaCloudStorage.entity.UserEntity;
import ru.netology.diplomaCloudStorage.repository.FileRepository;
import ru.netology.diplomaCloudStorage.repository.TokenRepository;
import ru.netology.diplomaCloudStorage.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    TokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
        fileRepository.deleteAll();
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @Test
    public void testLogin() {
        userRepository.save(new UserEntity(1L, "semashkevich", "qwerty12345"));
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        final LoginRequest operation = new LoginRequest("semashkevich", "qwerty12345");
        final HttpEntity<LoginRequest> request = new HttpEntity<>(operation, headers);
        final ResponseEntity<String> result = this.restTemplate.postForEntity("/login", request, String.class);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getBody());
    }

    @Test
    public void testLogout() {
        final String authToken = "semaroman";
        tokenRepository.putTokenAndLogin(authToken, "semashkevich");

        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", authToken);
        final HttpEntity<Void> request = new HttpEntity<>(null, headers);

        this.restTemplate.postForEntity("/logout", request, Void.class);

        Assertions.assertFalse(tokenRepository
                .getLoginByToken(authToken.split(" ")[1].trim()).isPresent());
    }

    @Test
    public void testUploadFile() {
        userRepository.save(new UserEntity(1L, "semashkevich", "qwerty12345"));

        final String authToken = "semaroman";
        tokenRepository.putTokenAndLogin(authToken.split(" ")[1].trim(), "semashkevich");

        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", authToken);

        final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("test.txt"));

        final HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(parts, headers);

        this.restTemplate.postForEntity("/file?auth-token=" + authToken + "&filename=test.txt",
                request, Void.class);

        Optional<UserEntity> user = userRepository.findUserByLogin("semashkevich");
        if (user.isPresent()) {
            Long userId = user.get().getId();
            final List<Long> fileIds = fileRepository
                    .findFilesByUserIdAndName(userId, "test.txt");
            Assertions.assertFalse(fileIds.isEmpty());
        }
    }


    @Test
    public void testDeleteFile() {
        userRepository.save(new UserEntity(1L, "semashkevich", "qwerty12345"));

        final String authToken = "semaroman";
        tokenRepository.putTokenAndLogin(authToken.split(" ")[1].trim(), "semashkevich");

        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", authToken);

        // Добавление файла
        final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("test.txt"));

        final HttpEntity<MultiValueMap<String, Object>> addRequest = new HttpEntity<>(parts, headers);

        this.restTemplate.postForEntity("/file?auth-token=" + authToken + "&filename=test.txt",
                addRequest, Void.class);

        // Удаление файла
        final HttpEntity<Void> deleteRequest = new HttpEntity<>(null, headers);
        this.restTemplate.exchange("/file?auth-token=" + authToken + "&filename=test.txt",
                HttpMethod.DELETE, deleteRequest, Void.class);

        Optional<UserEntity> user = userRepository.findUserByLogin("semashkevich");
        if (user.isPresent()) {
            Long userId = user.get().getId();
            final List<Long> fileIds = fileRepository
                    .findFilesByUserIdAndName(userId, "test.txt");
            Assertions.assertTrue(fileIds.isEmpty());
        }
    }

    @Test
    public void testDownloadFile() {
        UserEntity user = new UserEntity(1L, "semashkevich", "qwerty12345");
        userRepository.save(user);

        user.setId(userRepository.findUserByLogin("admin").get().getId());

        final String authToken = "semaroman";
        tokenRepository.putTokenAndLogin(authToken.split(" ")[1].trim(), "semashkevich");

        fileRepository.save(new FileEntity("test.txt", new byte[]{49, 51, 50}, user));

        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", authToken);

        final HttpEntity<Void> request = new HttpEntity<>(null, headers);

        final ResponseEntity<byte[]> result = this.restTemplate.exchange(
                "/file?auth-token=" + authToken + "&filename=test.txt",
                HttpMethod.GET, request, byte[].class);

        Assertions.assertNotNull(result.getBody());
        Assertions.assertArrayEquals(new byte[]{49, 51, 50}, result.getBody());
    }

    @Test
    public void testRenameFile() {
        UserEntity user = new UserEntity(1L, "semashkevich", "qwerty12345");
        userRepository.save(user);

        user.setId(userRepository.findUserByLogin("semashkevich").get().getId());

        final String authToken = "semaroman";
        tokenRepository.putTokenAndLogin(authToken.split(" ")[1].trim(), "semashkevich");

        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", authToken);

        // Добавление файла
        final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("test.txt"));

        final HttpEntity<MultiValueMap<String, Object>> addRequest = new HttpEntity<>(parts, headers);

        this.restTemplate.postForEntity("/file?auth-token=" + authToken + "&filename=test.txt",
                addRequest, Void.class);

        // Переименование файла
        final HttpEntity<FileNameRequest> request =
                new HttpEntity<>(new FileNameRequest("test1.txt"), headers);

        this.restTemplate.exchange("/file?filename=test.txt",
                HttpMethod.PUT, request, Void.class);

        List<Long> initialFileIds = fileRepository.findFilesByUserIdAndName(user.getId(),
                "test.txt");
        Assertions.assertTrue(initialFileIds.isEmpty());

        List<Long> renamedFileIds = fileRepository.findFilesByUserIdAndName(user.getId(),
                "test1.txt");
        Assertions.assertFalse(renamedFileIds.isEmpty());
    }

    @Test
    public void getFileListTest() {
        UserEntity user = new UserEntity(1L, "semashkevich", "qwerty12345");
        userRepository.save(user);

        user.setId(userRepository.findUserByLogin("semashkevich").get().getId());

        final String authToken = "semaroman";
        tokenRepository.putTokenAndLogin(authToken.split(" ")[1].trim(), "semashkevich");

        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", authToken);

        // Добавление первого файла
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("test.txt"));

        HttpEntity<MultiValueMap<String, Object>> addRequest = new HttpEntity<>(parts, headers);

        this.restTemplate.postForEntity("/file?auth-token=" + authToken + "&filename=test.txt",
                addRequest, Void.class);

        // Добавление второго файла
        parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("test2.txt"));

        addRequest = new HttpEntity<>(parts, headers);

        this.restTemplate.postForEntity("/file?auth-token=" + authToken + "&filename=test2.txt",
                addRequest, Void.class);

        // Добавление трпетьего файла
        parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("test3.txt"));

        addRequest = new HttpEntity<>(parts, headers);

        this.restTemplate.postForEntity("/file?auth-token=" + authToken + "&filename=test3.txt",
                addRequest, Void.class);

        final HttpEntity<Void> request = new HttpEntity<>(null, headers);

        final ResponseEntity<Object> result = this.restTemplate.exchange("/list?limit=10",
                HttpMethod.GET, request, Object.class);

        Assertions.assertNotNull(result.getBody());
    }
}

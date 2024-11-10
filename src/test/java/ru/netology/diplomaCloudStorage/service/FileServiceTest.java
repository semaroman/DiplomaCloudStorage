package ru.netology.diplomaCloudStorage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.diplomaCloudStorage.entity.FileEntity;
import ru.netology.diplomaCloudStorage.entity.UserEntity;
import ru.netology.diplomaCloudStorage.repository.FileRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class FileServiceTest {
    public static final Long ID = 1L;
    public static final String FILE = "file";
    public static final String UNEXISTING_FILE = "unexistingFile";

    private final UserEntity user = new UserEntity(1L, "semashkevich", "qwerty12345");
    private final FileEntity file = new FileEntity(1L, FILE, new byte[]{0, 1, 2}, user);
    private final Long fileId = Long.parseLong(file.getId().toString());
    private final Long unexistingFileId = 100L;


    private final FileRepository fileRepository = createFileRepositoryMock();
    private final FileService fileService = new FileService(fileRepository);

    private FileRepository createFileRepositoryMock() {
        final FileRepository fileRepository = Mockito.mock(FileRepository.class);

        ArrayList<Long> existingFileIds = new ArrayList<>();
        existingFileIds.add(fileId);

        ArrayList<Long> nonExistingFileIds = new ArrayList<>();

        when(fileRepository.findFilesByUserIdAndName(user.getId(), FILE))
                .thenReturn(existingFileIds);

        when(fileRepository.findFilesByUserIdAndName(user.getId(), UNEXISTING_FILE))
                .thenReturn(nonExistingFileIds);

        when(fileRepository.findFilesById(fileId))
                .thenReturn(file);

        when(fileRepository.findFilesById(unexistingFileId))
                .thenReturn(null);

        when(fileRepository.findFilesByUserIdWithLimit(user.getId(), 1))
                .thenReturn(List.of(file));

        return fileRepository;
    }

    @Test
    void getExistingFileTest() {
        final byte[] expectedFile = new byte[]{0, 1, 2};
        final byte[] file = fileService.getFile(FILE, user).getContent();
        Assertions.assertArrayEquals(expectedFile, file);
    }

    @Test
    void getNonExistingFileTest() {
        Assertions.assertThrows(RuntimeException.class,
                () -> fileService.getFile(UNEXISTING_FILE, user));
    }

    @Test
    void deleteExistingFileTest() {
        Assertions.assertDoesNotThrow(() -> fileService.deleteFile(FILE, user));
    }

    @Test
    void deleteNonExistingFileTest() {
        Assertions.assertThrows(RuntimeException.class,
                () -> fileService.deleteFile(UNEXISTING_FILE, user));
    }

    @Test
    void renameExistingFileTest() {
        Assertions.assertDoesNotThrow(() -> fileService.renameFile(FILE,
                user, UNEXISTING_FILE));
    }

    @Test
    void renameNonExistingFileTest() {
        Assertions.assertThrows(RuntimeException.class,
                () -> fileService.renameFile(UNEXISTING_FILE,
                        user, FILE));
    }

    @Test
    void getFileList() {
        final byte[] expectedFileContent = new byte[]{0, 1, 2};
        final FileEntity expectedFile = new FileEntity(ID, FILE,
                expectedFileContent, user);
        final List<FileEntity> expectedFileList = List.of(expectedFile);

        final List<FileEntity> actualFileList = fileService.getAllFiles(user, 1);
        Assertions.assertEquals(expectedFileList, actualFileList);
    }
}

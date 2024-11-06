package ru.netology.diplomaCloudStorage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.diplomaCloudStorage.dto.FileDescriptionResponse;
import ru.netology.diplomaCloudStorage.entity.FileEntity;
import ru.netology.diplomaCloudStorage.repository.FileRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

class FileServiceTest {
    public static final String FILE_EXISTS = "fileExists";
    public static final String FILE_IS_NOT_EXISTS = "fileIsNotExists";

    private final FileEntity existingFileEntity = new FileEntity(FILE_EXISTS, new byte[]{0, 1, 2});

    private final FileRepository fileRepository = createFileRepositoryMock();
    private final FileService fileService = new FileService(fileRepository);

    private FileRepository createFileRepositoryMock() {
        final FileRepository fileRepository = Mockito.mock(FileRepository.class);

        when(fileRepository.findById(FILE_EXISTS)).thenReturn(Optional.of(existingFileEntity));
        when(fileRepository.findById(FILE_IS_NOT_EXISTS)).thenReturn(Optional.empty());

        when(fileRepository.existsById(FILE_EXISTS)).thenReturn(true);
        when(fileRepository.existsById(FILE_IS_NOT_EXISTS)).thenReturn(false);

        when(fileRepository.getFiles(1)).thenReturn(List.of(existingFileEntity));

        return fileRepository;
    }

    @Test
    void getFile() {
        final byte[] expectedFile = new byte[]{0, 1, 2};
        final byte[] file = fileService.getFile(FILE_EXISTS);
        Assertions.assertArrayEquals(expectedFile, file);
    }

    @Test
    void getFile_failed() {
        Assertions.assertThrows(RuntimeException.class, () -> fileService.getFile(FILE_IS_NOT_EXISTS));
    }

    @Test
    void deleteFile() {
        Assertions.assertDoesNotThrow(() -> fileService.deleteFile(FILE_EXISTS));
    }

    @Test
    void deleteFile_failed() {
        Assertions.assertThrows(RuntimeException.class, () -> fileService.deleteFile(FILE_IS_NOT_EXISTS));
    }

    @Test
    void editFileName() {
        Assertions.assertDoesNotThrow(() -> fileService.editFileName(FILE_EXISTS, FILE_IS_NOT_EXISTS));
    }

    @Test
    void editFileName_failed() {
        Assertions.assertThrows(RuntimeException.class, () -> fileService.editFileName(FILE_IS_NOT_EXISTS, FILE_EXISTS));
    }

    @Test
    void getFileList() {
        final List<FileDescriptionResponse> expectedFileList = List.of(new FileDescriptionResponse(FILE_EXISTS, 3));
        final List<FileDescriptionResponse> fileList = fileService.getFileList(1);
        Assertions.assertEquals(expectedFileList, fileList);
    }
}
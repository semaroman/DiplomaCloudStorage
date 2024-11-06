package ru.netology.diplomaCloudStorage.service;

import org.springframework.stereotype.Service;
import ru.netology.diplomaCloudStorage.dto.FileDescriptionResponse;
import ru.netology.diplomaCloudStorage.entity.FileEntity;
import ru.netology.diplomaCloudStorage.repository.FileRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public synchronized void addFile(String filename, byte[] file) {
        fileRepository.save(new FileEntity(filename, file));
    }

    public synchronized void deleteFile(String filename) {
        if (!fileRepository.existsById(filename)) {
            throw new RuntimeException("Файл " + filename + " не найден");
        }
        fileRepository.deleteById(filename);
    }

    public byte[] getFile(String filename) {
        final FileEntity file = getFileByName(filename);
        return file.getFileContent();
    }

    public synchronized void editFileName(String oldFilename, String newFilename) {
        final FileEntity fileEntity = getFileByName(oldFilename);
        final FileEntity newFileEntity = new FileEntity(newFilename, fileEntity.getFileContent());
        fileRepository.delete(fileEntity);
        fileRepository.save(newFileEntity);
    }

    public List<FileDescriptionResponse> getFileList(int limit) {
        final List<FileEntity> files = fileRepository.getFiles(limit);
        return files.stream()
                .map(file -> new FileDescriptionResponse(file.getFileName(), file.getFileContent().length))
                .collect(Collectors.toList());
    }

    private FileEntity getFileByName(String filename) {
        return fileRepository.findById(filename).orElseThrow(() ->
                new RuntimeException("Файл " + filename + " не найден"));
    }
}
package ru.netology.diplomaCloudStorage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.diplomaCloudStorage.entity.FileEntity;
import ru.netology.diplomaCloudStorage.entity.UserEntity;
import ru.netology.diplomaCloudStorage.exception.DuplicateFileNameException;
import ru.netology.diplomaCloudStorage.exception.FileNotFoundException;
import ru.netology.diplomaCloudStorage.repository.FileRepository;

import java.util.List;

@Slf4j
@Service
public class FileService {
    private final FileRepository fileRepository;
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public synchronized void addFile(String name, byte[] content, UserEntity user) {
        log.info("User id {}", user.getId());
        fileRepository.save(new FileEntity(name, content, user));
    }

    public synchronized void deleteFile(String name, UserEntity user) {
        log.info("User id {}", user.getId());
        List<Long> ids = fileRepository.findFilesByUserIdAndName(user.getId(), name);
        if (ids.isEmpty()) {
            log.info("File {} is not found for user {}", name, user.getLogin());
            throw new FileNotFoundException("Файл не найден");
        }

        for (Long id : ids) {
            log.info("File with id {} will be deleted", id);
            fileRepository.deleteById(id);
        }
    }

    public List<FileEntity> getAllFiles(UserEntity user, int limit) {
        log.info("User id {}", user.getId());
        return fileRepository.findFilesByUserIdWithLimit(user.getId(), limit);
    }

    public FileEntity getFile(String name, UserEntity user) {
        List<Long> ids = fileRepository.findFilesByUserIdAndName(user.getId(), name);
        if (ids.isEmpty()) {
            log.info("File {} is not found for user {}", name, user.getLogin());
            throw new FileNotFoundException("Файл не найден");
        }

        return fileRepository.findFilesById(ids.get(0));
    }

    public synchronized void renameFile(String oldName, UserEntity user, String newName) {
        List<Long> ids = fileRepository.findFilesByUserIdAndName(user.getId(), oldName);
        if (ids.isEmpty()) {
            log.info("File {} is not found for user {}", oldName, user.getLogin());
            throw new FileNotFoundException("Файл не найден");
        }

        List<Long> newIds = fileRepository.findFilesByUserIdAndName(user.getId(), newName);
        if (!newIds.isEmpty()) {
            log.info("User {} already has file {}", user.getLogin(), newName);
            throw new DuplicateFileNameException("У пользователя " + user.getId() +
                    " уже есть файл " + newName);
        }

        FileEntity file = this.getFile(oldName, user);
        file.setName(newName);
        fileRepository.save(file);
    }

    public byte[] downloadFile(String filename, UserEntity user) {
        List<Long> ids = fileRepository.findFilesByUserIdAndName(user.getId(), filename);
        if (ids.isEmpty()) {
            log.info("File {} is not found for user {}", filename, user.getLogin());
            throw new FileNotFoundException("Файл не найден");
        }

        final FileEntity file = this.getFile(filename, user);
        log.info("Download content for file {}", filename);
        return file.getContent();
    }
}

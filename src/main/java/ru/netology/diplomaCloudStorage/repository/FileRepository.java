package ru.netology.diplomaCloudStorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.netology.diplomaCloudStorage.entity.FileEntity;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    @Query(value = "select * from files f where f.user_id = ?1 order by f.id asc limit ?2",
            nativeQuery = true)
    List<FileEntity> findFilesByUserIdWithLimit(Long userId, int limit);

    @Query(value = "select id from files f where f.user_id = ?1 and f.name = ?2",
            nativeQuery = true)
    List<Long> findFilesByUserIdAndName(Long userId, String name);

    @Query(value = "select * from files f where f.id = ?1 limit 1",
            nativeQuery = true)
    FileEntity findFilesById(Long fileId);

    @Query(value = "select * from files", nativeQuery = true)
    List<FileEntity> findAllFiles();
}

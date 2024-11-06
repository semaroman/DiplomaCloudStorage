package ru.netology.diplomaCloudStorage.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.netology.diplomaCloudStorage.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, String> {
}
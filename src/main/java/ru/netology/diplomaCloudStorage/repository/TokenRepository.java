package ru.netology.diplomaCloudStorage.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.netology.diplomaCloudStorage.entity.TokenEntity;

@Repository
public interface TokenRepository extends CrudRepository<TokenEntity, String> {
}
package com.vlife.repository;

import com.vlife.model.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User getUserByTelegramUserId(Long telegramUserId);
    User getUserByUsername(String username);
}

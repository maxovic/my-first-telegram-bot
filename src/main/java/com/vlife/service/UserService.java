package com.vlife.service;

import com.vlife.model.entity.User;
import com.vlife.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Service
public class UserService {
    private static final String ENTITY_ALREADY_EXISTS_MESSAGE = "Пользователь с таким telegramUserId(%s) уже зарегистрирован.";
    private static final String ENTITY_NOT_EXISTS_MESSAGE = "Пользователь с таким telegramUserId(%s) не зарегистрирован.";

    @Autowired
    private UserRepository userRepository;

    public void addNewUser(Long telegramUserId, Boolean isRegistered) {
        if (!userExists(telegramUserId)) {
            User user = new User();
            user.setTelegramUserId(telegramUserId);
            user.setIsRegistered(isRegistered);
            userRepository.save(user);
        } else {
            throw new EntityExistsException(
                    String.format(
                            ENTITY_ALREADY_EXISTS_MESSAGE,
                            telegramUserId
                    )
            );
        }
    }

    public void deleteUser(Long telegramUserId) {
        User user = userRepository.getUserByTelegramUserId(telegramUserId);
        if (user != null) {
            userRepository.delete(user);
        } else {
            throw new EntityNotFoundException(
                    String.format(
                            ENTITY_NOT_EXISTS_MESSAGE,
                            telegramUserId
                    )
            );
        }
    }

    public void updateUsername(Long telegramUserId, String username) {
        User user = userRepository.getUserByTelegramUserId(telegramUserId);
        if (user != null) {
            user.setUsername(username);
            userRepository.save(user);
        } else {
            throw new EntityNotFoundException(
                    String.format(
                            ENTITY_NOT_EXISTS_MESSAGE,
                            telegramUserId
                    )
            );
        }
    }

    public void updateIsRegistered(Long telegramUserId, Boolean value) {
        User user = userRepository.getUserByTelegramUserId(telegramUserId);
        if (user != null) {
            user.setIsRegistered(value);
            userRepository.save(user);
        } else {
            throw new EntityNotFoundException(
                    String.format(
                            ENTITY_NOT_EXISTS_MESSAGE,
                            telegramUserId
                    )
            );
        }
    }

    public Boolean isRegistered(Long telegramUserId) {
        User user = userRepository.getUserByTelegramUserId(telegramUserId);
        if (user != null) {
            return user.getIsRegistered();
        } else {
            return false;
        }
    }

    public Boolean userExists(Long telegramUserId) {
        return userRepository.getUserByTelegramUserId(telegramUserId) != null;
    }
}

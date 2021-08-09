package com.vlife.service;

import com.vlife.model.entity.UserPreference;
import com.vlife.repository.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;

@Service
public class UserPreferenceService {
    private static final String ENTITY_ALREADY_EXISTS_MESSAGE = "UserPreference для пользователя с telegramUserId='%s' уже существует.";

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    public void addUserPreference(Long telegramUserId, String breed) {
        if (userPreferenceRepository.getUserPreferenceByTelegramUserIdAndBreed(telegramUserId, breed) == null) {
            UserPreference userPreference = new UserPreference();
            userPreference.setTelegramUserId(telegramUserId);
            userPreference.setBreed(breed);
            userPreference.setIsLiked(true);
            userPreferenceRepository.save(userPreference);
        } else {
            throw new EntityExistsException(
                    String.format(
                            ENTITY_ALREADY_EXISTS_MESSAGE,
                            telegramUserId
                    )
            );
        }
    }

    public void switchIsLiked(UserPreference userPreference) {
        userPreference.setIsLiked(!userPreference.getIsLiked());
        userPreferenceRepository.save(userPreference);
    }
}

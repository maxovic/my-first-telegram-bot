package com.vlife.repository;

import com.vlife.model.entity.UserPreference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends CrudRepository<UserPreference, Integer> {
    Optional<UserPreference> findUserPreferenceByTelegramUserIdAndBreed(Long telegramUserId, String breed);
    UserPreference getUserPreferenceByTelegramUserIdAndBreed(Long telegramUserId, String breed);
    List<UserPreference> getAllByTelegramUserIdAndIsLiked(Long telegramUserId, Boolean isLiked);

}

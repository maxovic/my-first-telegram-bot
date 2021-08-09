package com.vlife.service;

import com.vlife.model.entity.UserPreference;
import com.vlife.repository.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

@Service
public class BotResponseService {
    private static final String I_DONT_KNOW = "К сожалению, я еще не достаточно умен чтобы понимать такие команды.";
    private static final String AVAILABLE_BREED_LIST = "Список доступных пород.";
    private static final String FAVORITE_BREED_LIST = "Ваши любимые породы.";
    private static final String BREED_IMAGE = "Фото собаки породы *%s*.";
    private static final String LIKE_BREED = "*%s* добавлено в избранное.";
    private static final String DISLIKE_BREED = "*%s* удалено из избранного.";
    private static final String MARKDOWN_PARSE_MODE = "markdown";
    private static final String HEART_EMOJI = ":heart:";
    private static final String BROKEN_HEART_EMOJI = ":broken_heart:";
    private static final String START_MESSAGE = "Чем я могу Вам помочь?";

    @Autowired
    private DogApiService dogApiService;
    @Autowired
    private ButtonManagementService buttonManagementService;
    @Autowired
    private UserPreferenceService userPreferenceService;
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    public SendMessage getStartMessage(Message receivedMessage) {
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(receivedMessage.getChatId().toString());
        responseMessage.setText(START_MESSAGE);
        buttonManagementService.addMenuButtons(responseMessage);
        return responseMessage;
    }

    public SendMessage getAllBreedsMessage(String chatId) {
        List<String> breeds = dogApiService.getAllBreeds().subList(0, 9);
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(chatId);
        responseMessage.setText(AVAILABLE_BREED_LIST);
        buttonManagementService.addInlineBreedButtons(responseMessage, breeds);
        return responseMessage;
    }

    public SendMessage getAllLikedBreedsMessage(Message receivedMessage) {
        List<String> breeds = new ArrayList<>();
        List<UserPreference> userPreferences = userPreferenceRepository
                .getAllByTelegramUserIdAndIsLiked(receivedMessage.getFrom().getId(), true);
        for (UserPreference userPreference : userPreferences) {
            breeds.add(userPreference.getBreed());
        }
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(receivedMessage.getChatId().toString());
        responseMessage.setText(FAVORITE_BREED_LIST);
        buttonManagementService.addInlineBreedButtons(responseMessage, breeds);
        return responseMessage;
    }

    public SendPhoto getBreedImageMessage(CallbackQuery callbackQuery) {
        String breed = extractBreedName(callbackQuery.getData());
        String breedImageURL = dogApiService.getRandomImageURLByBreed(breed);
        InputFile inputFile = new InputFile();
        inputFile.setMedia(breedImageURL);
        SendPhoto responsePhoto = new SendPhoto();
        responsePhoto.setChatId(callbackQuery.getMessage().getChatId().toString());
        responsePhoto.setPhoto(inputFile);
        responsePhoto.setCaption(String.format(BREED_IMAGE, breed));
        responsePhoto.setParseMode(MARKDOWN_PARSE_MODE);
        buttonManagementService.addInlineLikeAndDislikeButton(responsePhoto, breed, getEmoji(callbackQuery));
        return responsePhoto;
    }

    public SendMessage getLikeAndDislikeResponseMessage(CallbackQuery callbackQuery) {
        Long telegramUserId = callbackQuery.getFrom().getId();
        String breed = extractBreedName(callbackQuery.getData());
        onClickLikeAndDislikeButton(telegramUserId, breed);
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(callbackQuery.getMessage().getChatId().toString());
        responseMessage.setParseMode(MARKDOWN_PARSE_MODE);
        if (userPreferenceRepository.getUserPreferenceByTelegramUserIdAndBreed(telegramUserId, breed).getIsLiked()) {
            responseMessage.setText(String.format(LIKE_BREED, breed));
        } else {
            responseMessage.setText(String.format(DISLIKE_BREED, breed));
        }
        return responseMessage;
    }

    public SendMessage getIDontKnowMessage(String chatId) {
        return SendMessage
                .builder()
                .chatId(chatId)
                .text(I_DONT_KNOW)
                .build();
    }

    private void onClickLikeAndDislikeButton(Long telegramUserId, String breed) {
        userPreferenceRepository.findUserPreferenceByTelegramUserIdAndBreed(telegramUserId, breed)
                .ifPresentOrElse(
                        userPreferenceService::switchIsLiked,
                        () -> userPreferenceService.addUserPreference(telegramUserId, breed)
                );
    }

    private String getEmoji(CallbackQuery callbackQuery) {
        Long telegramUserId = callbackQuery.getFrom().getId();
        String breed = extractBreedName(callbackQuery.getData());
        UserPreference userPreference = userPreferenceRepository
                .getUserPreferenceByTelegramUserIdAndBreed(telegramUserId, breed);
        if (userPreference != null && userPreference.getIsLiked()) {
            return BROKEN_HEART_EMOJI;
        } else {
            return HEART_EMOJI;
        }
    }

    private String extractBreedName(String command) {
        return command.substring(0, command.indexOf('|'));
    }
}

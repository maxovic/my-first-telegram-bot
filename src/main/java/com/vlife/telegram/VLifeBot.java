package com.vlife.telegram;

import com.vlife.configurations.BotConfiguration;
import com.vlife.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class VLifeBot extends TelegramLongPollingBot {
    public static final String START_COMMAND = "/start";
    public static final String GET_BREED_LIST_COMMAND = "список пород";
    public static final String GET_LIKED_BREED_LIST_COMMAND = "избранное";
    public static final String CANCEL_REGISTRATION_COMMAND = "cancel registration";
    public static final String CONFIRM_USERNAME_COMMAND = "confirm username";
    public static final String GET_BREED_IMAGE_COMMAND = "%s|image";
    public static final String LIKE_DISLIKE_BREED_COMMAND = "%s|like/dislike";
    private static final String IMAGE = "image";

    private final BotConfiguration botConfiguration;

    @Autowired
    private UserService userService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private BotResponseService botResponseService;

    public VLifeBot(BotConfiguration botConfiguration) {
        this.botConfiguration = botConfiguration;
    }

    @Override
    public String getBotUsername() {
        return botConfiguration.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getToken();
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleMessage(update.getMessage());
        } else {
            handleCallback(update.getCallbackQuery());
        }
    }

    private void handleMessage(Message message) {
        if (!userService.userExists(message.getFrom().getId())) {
            SendMessage responseMessage = registrationService.getWelcomeMessageForNewUser(message);
            executeSendMessage(responseMessage);
        } else if (!userService.isRegistered(message.getFrom().getId())) {
            SendMessage responseMessage = registrationService.getUsernameValidationMessage(message);
            executeSendMessage(responseMessage);
        } else if (START_COMMAND.equals(message.getText())) {
            SendMessage responseMessage = botResponseService.getStartMessage(message);
            executeSendMessage(responseMessage);
        } else if (GET_BREED_LIST_COMMAND.equals(message.getText())) {
            SendMessage responseMessage = botResponseService.getAllBreedsMessage(message.getChatId().toString());
            executeSendMessage(responseMessage);
        } else if (GET_LIKED_BREED_LIST_COMMAND.equals(message.getText())) {
            SendMessage responseMessage = botResponseService.getAllLikedBreedsMessage(message);
            executeSendMessage(responseMessage);
        } else {
            SendMessage responseMessage = botResponseService.getIDontKnowMessage(message.getChatId().toString());
            executeSendMessage(responseMessage);
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        if (CANCEL_REGISTRATION_COMMAND.equals(callbackQuery.getData())) {
            SendMessage responseMessage = registrationService.getMessageForCancelRegistration(callbackQuery);
            executeSendMessage(responseMessage);
        } else if (CONFIRM_USERNAME_COMMAND.equals(callbackQuery.getData())) {
            SendMessage responseMessage = registrationService.getMessageForNewlyRegisteredUser(callbackQuery);
            executeSendMessage(responseMessage);
        } else if (callbackQuery.getData().contains(IMAGE)) {
            SendPhoto responsePhoto = botResponseService.getBreedImageMessage(callbackQuery);
            executeSendPhoto(responsePhoto);
        } else {
            SendMessage responseMessage = botResponseService.getLikeAndDislikeResponseMessage(callbackQuery);
            executeSendMessage(responseMessage);
        }
    }

    private void executeSendMessage(SendMessage responseMessage) {
        try {
            execute(responseMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void executeSendPhoto(SendPhoto responsePhoto) {
        try {
            execute(responsePhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

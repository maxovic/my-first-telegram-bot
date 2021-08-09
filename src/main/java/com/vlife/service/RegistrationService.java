package com.vlife.service;

import com.vlife.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class RegistrationService {
    private static final String USERNAME_ALREADY_EXISTS = "Это имя пользователя уже занято, пожалуйста, выберите другое.";
    private static final String SIGN_UP_SUCCESS = "*%s*, Вы успешно зарегистрировались.";
    private static final String CONFIRM_USERNAME = "Подтвердите, пожалуйста, свой username: *%s*";
    private static final String INTRODUCE_BOTSELF = "Меня зовут Чат-Бот, чем я могу Вам помочь?";
    private static final String SEE_YOU = "До скорой встречи, *%s*.";
    private static final String MARKDOWN_PARSE_MODE = "markdown";
    public static final String USERNAME_REQUIREMENTS = "К *username* предъявляются следующие требования:\n" +
            "1. *username* должен состоять только из подряд идущих латинских букв и цифр.\n" +
            "2. *username* должен начинаться с латинской буквы.\n" +
            "3. *username* должен состоять не менее чем из 3 символов и не более чем из 10 символов.\n";
    private static final String WELCOME_MESSAGE = "*%s*, добро пожаловать." +
            "Перед тем, как начать Вам нужно зарегистрироваться, " +
            "Вам нужно просто придумать *username*.\n\n" +
            USERNAME_REQUIREMENTS;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ButtonManagementService buttonManagementService;
    @Autowired
    private UserService userService;

    public SendMessage getWelcomeMessageForNewUser(Message receivedMessage) {
        Long telegramUserId = receivedMessage.getFrom().getId();
        String chatId = receivedMessage.getChatId().toString();
        String telegramUserFirstName = receivedMessage.getFrom().getFirstName();
        userService.addNewUser(telegramUserId, false);
        SendMessage responseMessage = constructMessage(String.format(WELCOME_MESSAGE, telegramUserFirstName), chatId);
        buttonManagementService.addInlineCancelRegistrationButton(responseMessage);
        return responseMessage;
    }

    public SendMessage getUsernameValidationMessage(Message receivedMessage) {
        String username = receivedMessage.getText();
        String chatId = receivedMessage.getChatId().toString();
        if (userRepository.getUserByUsername(username) != null) {
            return constructMessage(USERNAME_ALREADY_EXISTS, chatId);
        }
        if (username.length() < 3 || username.length() > 10 || !username.matches("[A-Za-z][A-Za-z0-9]*")) {
            return constructMessage(USERNAME_REQUIREMENTS, chatId);
        }
        userService.updateUsername(receivedMessage.getFrom().getId(), username);
        SendMessage responseMessage = constructMessage(String.format(CONFIRM_USERNAME, username), chatId);
        buttonManagementService.addInlineUsernameConfirmationButton(responseMessage);
        return responseMessage;
    }

    public SendMessage getMessageForNewlyRegisteredUser(CallbackQuery callbackQuery) {
        userService.updateIsRegistered(callbackQuery.getFrom().getId(), true);
        SendMessage responseMessage = constructMessage(
                String.format(SIGN_UP_SUCCESS + '\n' + INTRODUCE_BOTSELF, callbackQuery.getFrom().getFirstName()),
                callbackQuery.getMessage().getChatId().toString()
        );
        buttonManagementService.addMenuButtons(responseMessage);
        return responseMessage;
    }

    public SendMessage getMessageForCancelRegistration(CallbackQuery callbackQuery) {
        Long telegramUserId = callbackQuery.getFrom().getId();
        String telegramUserFirstName = callbackQuery.getFrom().getFirstName();
        String chatId = callbackQuery.getMessage().getChatId().toString();
        userService.deleteUser(telegramUserId);
        return constructMessage(
                String.format(SEE_YOU, telegramUserFirstName),
                chatId
        );
    }

    private SendMessage constructMessage(String text, String chatId) {
        return SendMessage
                .builder()
                .text(text)
                .chatId(chatId)
                .parseMode(MARKDOWN_PARSE_MODE)
                .build();
    }
}

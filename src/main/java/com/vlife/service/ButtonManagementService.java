package com.vlife.service;

import com.vdurmont.emoji.EmojiParser;
import com.vlife.telegram.VLifeBot;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

@Service
public class ButtonManagementService {
    private static final String CONFIRM_BUTTON_TEXT = "Подтвердить.";
    private static final String CANCEL_REGISTRATION_BUTTON_TEXT = "Отмена регистрации.";

    public void addInlineCancelRegistrationButton(SendMessage responseMessage) {
        InlineKeyboardMarkup keyboard = constructInlineSingleButtonKeyboardMarkup(
                CANCEL_REGISTRATION_BUTTON_TEXT,
                VLifeBot.CANCEL_REGISTRATION_COMMAND
        );
        responseMessage.setReplyMarkup(keyboard);
    }

    public void addInlineUsernameConfirmationButton(SendMessage responseMessage) {
        InlineKeyboardMarkup keyboard = constructInlineSingleButtonKeyboardMarkup(
                CONFIRM_BUTTON_TEXT,
                VLifeBot.CONFIRM_USERNAME_COMMAND
        );
        responseMessage.setReplyMarkup(keyboard);
    }

    public void addInlineLikeAndDislikeButton(SendPhoto responsePhoto, String breed, String emoji) {
        InlineKeyboardMarkup keyboard = constructInlineSingleButtonKeyboardMarkup(
                EmojiParser.parseToUnicode(emoji),
                String.format(VLifeBot.LIKE_DISLIKE_BREED_COMMAND, breed)
        );
        responsePhoto.setReplyMarkup(keyboard);
    }

    public void addMenuButtons(SendMessage responseMessage) {
        KeyboardRow row = new KeyboardRow();
        row.add(VLifeBot.GET_BREED_LIST_COMMAND);
        row.add(VLifeBot.GET_LIKED_BREED_LIST_COMMAND);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(List.of(row));
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        responseMessage.setReplyMarkup(markup);
    }

    public void addInlineBreedButtons(SendMessage responseMessage, List<String> breeds) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0, j = 0; i < Math.min(9, breeds.size()); i++, j = i % 3) {
            if (j == 0) {
                row = new ArrayList<>();
                rows.add(row);
            }
            InlineKeyboardButton button = constructInlineSingleButton(
                    breeds.get(i),
                    String.format(VLifeBot.GET_BREED_IMAGE_COMMAND, breeds.get(i))
            );
            row.add(button);
        }
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder().keyboard(rows).build();
        responseMessage.setReplyMarkup(keyboard);
    }

    private InlineKeyboardMarkup constructInlineSingleButtonKeyboardMarkup(String buttonText, String callBackData) {
        InlineKeyboardButton button = constructInlineSingleButton(buttonText, callBackData);
        return InlineKeyboardMarkup
                .builder()
                .keyboard(new ArrayList<>(List.of(List.of(button))))
                .build();
    }

    private InlineKeyboardButton constructInlineSingleButton(String buttonText, String callBackData) {
        return InlineKeyboardButton
                .builder()
                .text(buttonText)
                .callbackData(callBackData)
                .build();
    }
}

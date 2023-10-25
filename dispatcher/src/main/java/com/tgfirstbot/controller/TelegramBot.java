package com.tgfirstbot.controller;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
         return botName;
    }
    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var originalMassge = update.getMessage();
        log.debug(originalMassge.getText());

        var response = new SendMessage();
        response.setChatId(originalMassge.getChatId().toString());
        response.setText("Hello my fried");
        sendAnswerMessage(response);
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message !=null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }
}

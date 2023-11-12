package ru.tgfirstbot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;


@Log4j
@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramWebhookBot {

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.url}")
    private  String botUrl;
    private final UpdateProcessor updateProcessor;
    @Override
    public String getBotUsername() {
         return botName;
    }
    @Override
    public String getBotToken() {
        return botToken;
    }
    @PostConstruct
    public void init () {
        updateProcessor.registerBot(this);
        try{
            var setWebhook = SetWebhook.builder()
                    .url(botUrl)
                    .build();
            this.setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            log.error(e);
        }
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
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }
    @Override
    public String getBotPath() {
        return "/update";
    }
}

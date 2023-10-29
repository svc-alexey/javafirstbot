package ru.tgfirstbot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void proccesTextMessage(Update update);
}

package ru.tgfirstbot.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tgfirstbot.entity.AppDocument;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
}

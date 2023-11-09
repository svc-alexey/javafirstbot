package ru.tgfirstbot.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tgfirstbot.entity.AppDocument;
import ru.tgfirstbot.entity.AppPhoto;
import ru.tgfirstbot.service.enums.LinkType;


public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long docId, LinkType linkType);
}

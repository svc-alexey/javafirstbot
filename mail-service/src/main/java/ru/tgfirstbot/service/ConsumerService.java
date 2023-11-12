package ru.tgfirstbot.service;

import ru.tgfirstbot.dto.MailParams;

public interface ConsumerService {
    void consumeRegistrationMail (MailParams mailParams);
}

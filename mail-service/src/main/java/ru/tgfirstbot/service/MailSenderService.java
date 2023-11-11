package ru.tgfirstbot.service;

import ru.tgfirstbot.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}

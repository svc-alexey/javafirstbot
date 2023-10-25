package ru.tgfirstbot.controller;


import ru.tgfirstbot.service.UpdateProducer;
import ru.tgfirstbot.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static ru.tgfirstbot.model.RabbitQueue.*;

@Component
@Log4j
public class UpadateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public UpadateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Recive update is null");
            return;
        }
        if (update.getMessage() != null) {
            distributeMessageByType(update);
        } else log.error("Recive unsopperted message type " + update);
    }

    private void distributeMessageByType(Update update) {
        var message = update.getMessage();
        if (message.getText() != null) {
            processTextMessage(update);
        } else if (message.getDocument() != null) {
            processDocMessage(update);
        } else if (message.getPhoto() != null) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessage(update);
        }
    }

    private void setUnsupportedMessage(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithTest(update,
                "Неподдерживаемый тип сообщения");
        setView(sendMessage);
    }

    private void setFileReciveView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithTest(update,
                "Файл получен! Обрабатывется...");
        setView(sendMessage);
    }
    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileReciveView(update);
    }

    public void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    public void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileReciveView(update);
    }

}

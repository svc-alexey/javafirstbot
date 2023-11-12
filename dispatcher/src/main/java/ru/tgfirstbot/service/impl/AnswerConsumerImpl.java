package ru.tgfirstbot.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.tgfirstbot.controller.UpdateProcessor;
import ru.tgfirstbot.service.AnswerConsumer;

import static ru.tgfirstbot.model.RabbitQueue.ANSWER_MESSAGE;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
   private final UpdateProcessor updateProcessor;

    public AnswerConsumerImpl(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sandMessage) {
        updateProcessor.setView(sandMessage);
    }
}

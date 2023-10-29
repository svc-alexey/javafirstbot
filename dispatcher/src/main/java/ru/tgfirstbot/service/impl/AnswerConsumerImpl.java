package ru.tgfirstbot.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.tgfirstbot.controller.UpdateController;
import ru.tgfirstbot.service.AnswerConsumer;

import static ru.tgfirstbot.model.RabbitQueue.ANSWER_MESSAGE;

public class AnswerConsumerImpl implements AnswerConsumer {
   private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consumer(SendMessage sandMessage) {
        updateController.setView(sandMessage);
    }
}

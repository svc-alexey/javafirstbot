package ru.tgfirstbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.tgfirstbot.dto.MailParams;
import ru.tgfirstbot.service.ConsumerService;
import ru.tgfirstbot.service.MailSenderService;

@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MailSenderService mailSenderService;


    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.registration-mail}")
    public void consumeRegistrationMail(MailParams mailParams) {
        mailSenderService.send(mailParams);
    }
}

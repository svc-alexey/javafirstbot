package ru.tgfirstbot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tgfirstbot.CryptoTool;
import ru.tgfirstbot.dao.AppUserDAO;
import ru.tgfirstbot.dto.MailParams;
import ru.tgfirstbot.entity.AppUser;
import ru.tgfirstbot.service.AppUserService;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static ru.tgfirstbot.entity.enums.UserState.BASIC_STATE;
import static ru.tgfirstbot.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;

@Log4j
@RequiredArgsConstructor
@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String registrationMailQueue;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()) {
            return "Вы уже зарегистрированы!";
        } else if (appUser.getEmail() != null) {
            return "Вам на почту было отправлено письмо. "
                    + " Перейдите по ссылке в письме для подтверждения регистрации.";
        }
        appUser.setState(WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);
        return "Введите пожалуйста ваш Email";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            var emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            return "Введите, пожаулуйста, корректный Email. Для отмены команды введите /cancel";
        }

        var appUserOptional = appUserDAO.findByEmail(email);
        if (appUserOptional.isEmpty()) {
            appUser.setEmail(email);
            appUser.setState(BASIC_STATE);
            appUser = appUserDAO.save(appUser);

            var cryptoUserId = cryptoTool.hashOf(appUser.getId());
            sendRegistrationMail(cryptoUserId, email);

            return "Вам на почту было отправлено письмо."
                    + "Перейдите по ссылке в письме для подтверждения регистрации.";
        } else {
            return "Этот Email уже используеться. Введите корретный Email"
                    + " Для отмены команды введите /cancel";
        }
    }

    private void sendRegistrationMail(String cryptoUserId, String email) {

        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .mailTo(email)
                .build();

        rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);
    }

}

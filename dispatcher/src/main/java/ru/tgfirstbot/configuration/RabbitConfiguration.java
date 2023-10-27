package ru.tgfirstbot.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ru.tgfirstbot.model.RabbitQueue.*;

@Configuration
public class RabbitConfiguration {
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMeassageQueue () {
        return new Queue(TEXT_MESSAGE_UPDATE);
    }
    @Bean
    public Queue docMeassageQueue () {
        return new Queue(DOC_MESSAGE_UPDATE);
    }
    @Bean
    public Queue photoMeassageQueue () {
        return new Queue(PHOTO_MESSAGE_UPDATE);
    }
    @Bean
    public Queue answerMeassageQueue () {
        return new Queue(ANSWER_MESSAGE);
    }
}

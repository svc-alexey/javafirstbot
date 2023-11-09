package ru.tgfirstbot.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.tgfirstbot.dao.AppUserDAO;
import ru.tgfirstbot.dao.RawDataDAO;
import ru.tgfirstbot.entity.AppDocument;
import ru.tgfirstbot.entity.AppPhoto;
import ru.tgfirstbot.entity.AppUser;
import ru.tgfirstbot.entity.RawData;
import ru.tgfirstbot.exeptions.UploadFileExeption;
import ru.tgfirstbot.service.FileService;
import ru.tgfirstbot.service.MainService;
import ru.tgfirstbot.service.ProducerService;
import ru.tgfirstbot.service.enums.LinkType;
import ru.tgfirstbot.service.enums.ServiceCommands;

import static ru.tgfirstbot.entity.enums.UserState.BASIC_STATE;
import static ru.tgfirstbot.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.tgfirstbot.service.enums.ServiceCommands.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
    }

    @Override
    public void proccesTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);

        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        var sericeCommand = ServiceCommands.fromValue(text);

        if (CANCEL.equals(sericeCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //TODO добавить обработку емейла
        } else {
            log.error("Uknown user state: " + userState);
            output = "Неизвестная ошибка! Введщите /cancel и порпобуйте снова";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void proccesDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument doc = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(doc.getId(), LinkType.GET_DOC);
            var answer = "Документ успешно загружен! "
                    + "Ссылка для скачивания " + link;
            sendAnswer(answer, chatId);
        }catch (UploadFileExeption e) {
            log.error(e);
            String error = "К сожалению, загрузка файла не удалась. Повторите попыту позже";
            sendAnswer(error, chatId);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            var error = "Зарегистрируйтесь или активируйте "
            + "свою учетную запсь для загрузки контента";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
           var error = "Отмените текущую команду с помощью /cancel для отправки файла";
           sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    @Override
    public void proccesPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            var answer = "Фото успешно загружено! "
                    + "Ссылка для скачивания " + link;
            sendAnswer(answer, chatId);
        }catch (UploadFileExeption e) {
            log.error(e);
            String error = "К сожалению, загрузка фото не удалась. Повторите попыту позже";
            sendAnswer(error, chatId);
        }
    }

    private void sendAnswer(String output, Long chatId) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        var serviceCommands = ServiceCommands.fromValue(cmd);

        if (REGISTRARION.equals(serviceCommands)) {
            //TODO добавить реггистацию
            return "Команда временно недоступна";
        } else if (START.equals(serviceCommands)) {
            return "Приветсвую! Чтобы посмотреть весь списолк команд введите /help";
        } else if (HELP.equals(serviceCommands)) {
            return help();
        } else {
            return "Неизвестная комана! Чтобы посмотреть весь списолк команд введите /help";
        }
    }

    private String help() {
        return "Список дотсупных команд:\n"
                + "/cancel - отмена выпонения текущей команды;\n"
                + "/registration = регистрация пользователя";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена";
    }

    private  AppUser findOrSaveAppUser (Update update) {
        User telegramUser = update.getMessage().getFrom();
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }

        return  persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder().event(update).build();
        rawDataDAO.save(rawData);
    }


}

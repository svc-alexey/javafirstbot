package ru.tgfirstbot.service;

import org.springframework.core.io.FileSystemResource;
import ru.tgfirstbot.entity.AppDocument;
import ru.tgfirstbot.entity.AppPhoto;
import ru.tgfirstbot.entity.BinaryContent;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
}

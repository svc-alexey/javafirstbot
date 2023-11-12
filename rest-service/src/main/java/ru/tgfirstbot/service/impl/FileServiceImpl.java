package ru.tgfirstbot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import ru.tgfirstbot.CryptoTool;
import ru.tgfirstbot.dao.AppDocumentDAO;
import ru.tgfirstbot.dao.AppPhotoDAO;
import ru.tgfirstbot.entity.AppDocument;
import ru.tgfirstbot.entity.AppPhoto;
import ru.tgfirstbot.entity.BinaryContent;
import ru.tgfirstbot.service.FileService;

@Log4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final CryptoTool cryptoTool;

    @Override
    public AppDocument getDocument(String docId) {
        var id = cryptoTool.idOf(docId);
        if (id == null) {
            return null;
        }
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String photoId) {
        var id = cryptoTool.idOf(photoId);
        if (id == null) {
            return null;
        }
        return appPhotoDAO.findById(id).orElse(null);
    }
}

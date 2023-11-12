package ru.tgfirstbot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import ru.tgfirstbot.CryptoTool;
import ru.tgfirstbot.dao.AppDocumentDAO;
import ru.tgfirstbot.dao.AppPhotoDAO;
import ru.tgfirstbot.dao.BinaryContentDAO;
import ru.tgfirstbot.entity.AppDocument;
import ru.tgfirstbot.entity.AppPhoto;
import ru.tgfirstbot.entity.BinaryContent;
import ru.tgfirstbot.exeptions.UploadFileExeption;
import ru.tgfirstbot.service.FileService;
import ru.tgfirstbot.service.enums.LinkType;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


@Log4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    @Value("${link.address}")
    private String linkAddress;
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final CryptoTool cryptoTool;

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        var photoSizeCount = telegramMessage.getPhoto().size();
        var photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() - 1 : 0;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent presistentBinaryContent = getPersistantBinaryContent(response);

            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, presistentBinaryContent);
            return appPhotoDAO.save(transientAppPhoto);
        } else {
            throw new UploadFileExeption("Bad response from telegram service " + response);
        }
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent presistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(presistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {

        Document telegramDoc = telegramMessage.getDocument();

        String fileId = telegramDoc.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent presistentBinaryContent = getPersistantBinaryContent(response);

            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, presistentBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        } else {
            throw new UploadFileExeption("Bad response from telegram service " + response);
        }
    }

    private BinaryContent getPersistantBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    private String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }
    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent presistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileName(telegramDoc.getFileName())
                .docName(telegramDoc.getFileName())
                .binaryContent(presistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileExeption(e);
        }
        //TODO подумать над оптимизацией
        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileExeption(urlObj.toExternalForm(), e);
        }
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fileId
        );
    }

    @Override
    public String generateLink(Long docId, LinkType linkType) {
        var hash = cryptoTool.hashOf(docId);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }
}

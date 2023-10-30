package ru.tgfirstbot.exeptions;

public class UploadFileExeption extends RuntimeException {
    public UploadFileExeption(String message, Throwable cause) {super(message, cause);}
    public UploadFileExeption(String message) {super(message);}
    public UploadFileExeption(Throwable cause) {super(cause);}

}

package my.mapkn3.pikabuTelegramBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        try {
            Message message = update.getMessage();
            if (message.hasPhoto() && message.hasEntities()) {
                StringBuilder hashtagsBuilder = new StringBuilder();
                for (MessageEntity messageEntity : message.getEntities()) {
                    if (messageEntity.getType().equals("hashtag")) {
                        hashtagsBuilder.append(messageEntity.getText()).append(" ");
                    }
                }
                String hashtags = hashtagsBuilder.toString().trim();
                SendPhoto sendPhoto;
                for (PhotoSize photoSize : message.getPhoto()) {
                    sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(message.getChatId());
                    sendPhoto.setCaption(hashtags);
                    sendPhoto.setPhoto(photoSize.getFileId());
                    execute(sendPhoto);
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "Mapkn3_bot";
    }

    @Override
    public String getBotToken() {
        return "644265190:AAE4pUkB2UWXpyXjprgyz14hYKGDUnkAiqQ";
    }
}

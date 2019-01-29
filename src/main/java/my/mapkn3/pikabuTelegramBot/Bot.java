package my.mapkn3.pikabuTelegramBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private String hashtag = "";

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        if (updates.size() > 1) {
            updates.forEach(this::onUpdateReceived);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            System.out.println("Get message");
            Message message = update.getMessage();
            String text = message.getCaption();
            if (text != null) {
                hashtag = text;
            }
            System.out.println("Has photo? " + message.hasPhoto());
            if (message.hasPhoto()) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(message.getChatId());
                sendPhoto.setCaption(hashtag);
                sendPhoto.setPhoto(message.getPhoto().get(0).getFileId());
                System.out.println("Send photo");
                execute(sendPhoto);
            }
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(message.getChatId());
            deleteMessage.setMessageId(message.getMessageId());
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

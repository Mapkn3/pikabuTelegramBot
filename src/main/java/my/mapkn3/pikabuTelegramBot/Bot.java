package my.mapkn3.pikabuTelegramBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private String hashtag = "";
    private String author = "";
    private boolean isActive = false;

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        System.out.println("Get " + updates.size() + " updates. Is active: " + isActive);
        boolean allInOne = false;
        Message message = updates.get(0).getMessage();
        String caption = message.getCaption();
        if (caption != null) {
            if (caption.charAt(0) == '#') {
                allInOne = true;
                isActive = true;
                hashtag = caption;
                author = message.getFrom().getUserName();
            }
        }
        updates.forEach(this::onUpdateReceived);
        if (allInOne) {
            isActive = false;
            allInOne = false;
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            System.out.println("Get message");
            Message message = update.getMessage();
            System.out.println("Has text? " + message.hasText());
            if (message.hasText()) {
                if (message.getText().charAt(0) == '#') {
                    hashtag = message.getText();
                    author = message.getFrom().getUserName();
                    isActive = true;
                    System.out.println("Hashtag change to " + hashtag + " from " + author);
                } else {
                    isActive = false;
                }
            }
            System.out.println("Has photo? " + message.hasPhoto());
            if (message.hasPhoto()) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(message.getChatId());
                sendPhoto.setCaption(hashtag + " from " + author);
                sendPhoto.setPhoto(message.getPhoto().get(0).getFileId());
                if (isActive) {
                    System.out.println("Send photo");
                    execute(sendPhoto);
                }
            }
            System.out.println("Has animation? " + message.hasAnimation());
            if (message.hasAnimation()) {
                SendAnimation sendAnimation = new SendAnimation();
                sendAnimation.setChatId(message.getChatId());
                sendAnimation.setCaption(hashtag + " from " + author);
                sendAnimation.setAnimation(message.getAnimation().getFileId());
                if (isActive) {
                    System.out.println("Send animation");
                    execute(sendAnimation);
                }
            }
            System.out.println("Has document? " + message.hasDocument());
            if (message.hasDocument()) {
                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(message.getChatId());
                sendDocument.setCaption(hashtag + " from " + author);
                sendDocument.setDocument(message.getDocument().getFileId());
                if (isActive) {
                    System.out.println("Send document");
                    execute(sendDocument);
                }
            }
            if (isActive) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(message.getChatId());
                deleteMessage.setMessageId(message.getMessageId());
                execute(deleteMessage);
                System.out.println("Message deleted");
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

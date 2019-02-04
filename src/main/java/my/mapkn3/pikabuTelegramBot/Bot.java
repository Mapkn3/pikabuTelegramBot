package my.mapkn3.pikabuTelegramBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private String botUsername;
    private String botApiToken;

    private String hashtag;
    private String author;
    private boolean isActive;

    public Bot() {
        this.botUsername = System.getenv("BOT_USERNAME");
        this.botApiToken = System.getenv("BOT_API_TOKEN");

        this.hashtag = "";
        this.author = "";
        this.isActive = false;
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        System.out.println("Get " + updates.size() + " updates. Is active: " + isActive);
        updates.forEach(System.out::println);
        updates.forEach(this::onUpdateReceived);
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Message message = update.getMessage();
            if (message == null) {
                message = update.getEditedMessage();
            }

            String username = "unknown";
            String name = "unknown";
            if (message.getFrom().getUserName() != null) {
                username = "@" + message.getFrom().getUserName();
            }
            String lastName = message.getFrom().getLastName();
            String firstName = message.getFrom().getFirstName();
            if (lastName != null && firstName != null) {
                name = lastName + " " + firstName;
            } else {
                if (lastName != null) {
                    name = lastName;
                }
                if (firstName != null) {
                    name = firstName;
                }
            }
            System.out.println("Get message from " + name + " (" + username + ")");

            String description = "";
            if (message.getCaption() != null) {
                description = message.getCaption();
            }
            if (message.hasText()) {
                description = message.getText();
            }

            boolean isChangeHashtag = false;
            if (description.trim().isEmpty()) {
                if (description.charAt(0) == '#') {
                    isChangeHashtag = true;
                } else {
                    if (author.equals(username) || author.equals(name)) {
                        isActive = false;
                    }
                }
                if (isChangeHashtag) {
                    hashtag = description;
                    if (message.getFrom().getUserName() != null) {
                        author = username;
                    } else {
                        author = name;
                    }
                    isActive = true;
                    execute(new DeleteMessage().setChatId(message.getChatId()).setMessageId(message.getMessageId()));
                    System.out.println("Hashtag change to " + hashtag + " from " + author);
                }
            }


            if (isActive && (author.equals(username) || author.equals(name))) {
                boolean isDelete = false;
                System.out.println("Has photo? " + message.hasPhoto());
                if (message.hasPhoto()) {
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(message.getChatId());
                    sendPhoto.setCaption(hashtag + " from " + author);
                    sendPhoto.setPhoto(message.getPhoto().get(message.getPhoto().size()-1).getFileId());
                    execute(sendPhoto);
                    isDelete = true;
                }
                System.out.println("Has animation/document? " + message.hasAnimation() + "/" + message.hasDocument());
                if (message.hasAnimation()) {
                    SendAnimation sendAnimation = new SendAnimation();
                    sendAnimation.setChatId(message.getChatId());
                    sendAnimation.setCaption(hashtag + " from " + author);
                    sendAnimation.setAnimation(message.getAnimation().getFileId());
                    execute(sendAnimation);
                    isDelete = true;
                } else if (message.hasDocument()) {
                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setChatId(message.getChatId());
                    sendDocument.setCaption(hashtag + " from " + author);
                    sendDocument.setDocument(message.getDocument().getFileId());
                    execute(sendDocument);
                    isDelete = true;
                }
                if (message.hasVideo()) {
                    SendVideo sendVideo = new SendVideo();
                    sendVideo.setChatId(message.getChatId());
                    sendVideo.setCaption(hashtag + " from " + author);
                    sendVideo.setVideo(message.getVideo().getFileId());
                    execute(sendVideo);
                    isDelete = true;
                }
                if (isDelete) {
                    DeleteMessage deleteMessage = new DeleteMessage();
                    deleteMessage.setChatId(message.getChatId());
                    deleteMessage.setMessageId(message.getMessageId());
                    execute(deleteMessage);
                    System.out.println("Message deleted");
               }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botApiToken;
    }
}

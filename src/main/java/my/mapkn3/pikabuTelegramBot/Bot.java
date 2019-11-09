package my.mapkn3.pikabuTelegramBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    private String botUsername;
    private String botApiToken;

    private ChatState chatState;
    private boolean isActive;

    private Long AkciumKicumId = 255621638L;
    private Integer ArtemiyId = 413561670;
    private Integer PikaCG_botId= 1069351518;

    private enum TYPE {
        SIMPLE,
        HASHTAG,
        IMAGE,
        GIF,
        DOCUMENT,
        VIDEO,
        URL
    }

    public Bot() {
        this.chatState = new ChatState();
        this.botUsername = System.getenv("BOT_USERNAME");
        this.botApiToken = System.getenv("BOT_API_TOKEN");
        this.isActive = false;
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        System.out.println("Get " + updates.size() + " updates. Is active: " + this.isActive);
        updates.forEach(System.out::println);
        updates.forEach(this::onUpdateReceived);
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            chatState = chatState.updateChatState(update);

            Message message = chatState.getLastMessage();
            if (message.isReply()) {
                Message replyToMessage = message.getReplyToMessage();
                if (replyToMessage.getFrom().getBot() && replyToMessage.getFrom().getId().equals(PikaCG_botId)) {
                    Message lastMessageFromArtemiy = chatState.getLastMessageForUser(ArtemiyId);
                    if (lastMessageFromArtemiy != null) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setText(message.getText());
                        sendMessage.setReplyToMessageId(lastMessageFromArtemiy.getMessageId());
                        sendMessage.setChatId(message.getChatId());
                        execute(sendMessage);
                    }
                }
            }
            /*if (chatState.getLastMessage().hasText()) {
                if (chatState.getLastMessage().getText().toLowerCase().contains("#идеянедели") || chatState.getLastMessage().getText().toLowerCase().contains("#ин")) {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText(chatState.getLastMessage().getText());
                    sendMessage.setChatId(AkciumKicumId);
                    execute(sendMessage);
                    return;
                }
            }

            System.out.println("Get message from " + chatState.getName() + " (" + chatState.getUsername() + ")");

            if (chatState.isChangeHashtag()) {
                this.isActive = true;
                System.out.println("Switch isActive to on");
            }

            if (this.isActive && chatState.fromAuthor()) {
                boolean isDelete = false;
                if (chatState.isChangeHashtag()) {
                    isDelete = true;
                    if (update.getMessage() == null) {
                        this.isActive = false;
                        System.out.println("Switch isActive to off");
                    }
                }
                System.out.println("Has text? " + chatState.getLastMessage().hasText());
                if (chatState.getLastMessage().hasText() && !chatState.isChangeHashtag()) {
                    if (chatState.getLastMessage().getText().startsWith("http")) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(chatState.getLastMessage().getChatId());
                        sendMessage.setText(chatState.getLastMessage().getText() + " from " + chatState.getAuthor());
                        execute(sendMessage);
                        isDelete = true;
                    } else {
                        this.isActive = false;
                        System.out.println("Switch isActive to off");
                    }
                }
                System.out.println("Has photo? " + chatState.getLastMessage().hasPhoto());
                if (chatState.getLastMessage().hasPhoto()) {
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(chatState.getLastMessage().getChatId());
                    sendPhoto.setCaption(chatState.getHashtag() + " from " + chatState.getAuthor());
                    sendPhoto.setPhoto(chatState.getLastMessage().getPhoto().get(chatState.getLastMessage().getPhoto().size() - 1).getFileId());
                    execute(sendPhoto);
                    isDelete = true;
                }
                System.out.println("Has animation/document? " + chatState.getLastMessage().hasAnimation() + "/" + chatState.getLastMessage().hasDocument());
                if (chatState.getLastMessage().hasAnimation()) {
                    SendAnimation sendAnimation = new SendAnimation();
                    sendAnimation.setChatId(chatState.getChatId());
                    sendAnimation.setCaption(chatState.getHashtag() + " from " + chatState.getAuthor());
                    sendAnimation.setAnimation(chatState.getLastMessage().getAnimation().getFileId());
                    execute(sendAnimation);
                    isDelete = true;
                } else if (chatState.getLastMessage().hasDocument()) {
                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setChatId(chatState.getChatId());
                    sendDocument.setCaption(chatState.getHashtag() + " from " + chatState.getAuthor());
                    sendDocument.setDocument(chatState.getLastMessage().getDocument().getFileId());
                    execute(sendDocument);
                    isDelete = true;
                }
                System.out.println("Has video? " + chatState.getLastMessage().hasVideo());
                if (chatState.getLastMessage().hasVideo()) {
                    SendVideo sendVideo = new SendVideo();
                    sendVideo.setChatId(chatState.getLastMessage().getChatId());
                    sendVideo.setCaption(chatState.getHashtag() + " from " + chatState.getAuthor());
                    sendVideo.setVideo(chatState.getLastMessage().getVideo().getFileId());
                    execute(sendVideo);
                    isDelete = true;
                }
                if (isDelete) {
                    deleteMessage(chatState.getLastMessage());
                }
            }*/
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void deleteMessage(Message message) throws TelegramApiException {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(message.getChatId());
        deleteMessage.setMessageId(message.getMessageId());
        execute(deleteMessage);
        System.out.println("Message deleted");
    }

    private TYPE getMessageType(Message message) {
        if (message.hasText()) {
            String text = message.getText();
            if (!text.trim().isEmpty() && text.charAt(0) == '#' && text.length() > 1) {
                return TYPE.HASHTAG;
            }
            if (text.startsWith("http")) {
                return TYPE.URL;
            }
        }
        if (message.hasPhoto()) {
            return TYPE.IMAGE;
        }
        if (message.hasAnimation()) {
            return TYPE.GIF;
        }
        if (message.hasDocument()) {
            return TYPE.DOCUMENT;
        }
        if (message.hasVideo()) {
            return TYPE.VIDEO;
        }
        return TYPE.SIMPLE;
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


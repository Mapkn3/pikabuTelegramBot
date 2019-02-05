package my.mapkn3.pikabuTelegramBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private String botUsername;
    private String botApiToken;

    private ChatState chatState;
    private boolean isActive;

    private enum TYPE {
        HASHTAG,
        IMAGE,
        DOCUMENT,
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
        System.out.println("Get " + updates.size() + " updates. Is active: " + isActive);
        updates.forEach(System.out::println);
        updates.forEach(this::onUpdateReceived);
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            chatState = chatState.updateChatState(update);

            System.out.println("Get message from " + chatState.getName() + " (" + chatState.getUsername() + ")");

            if (chatState.isChangeHashtag()) {
                isActive = true;
            }

            if (isActive && chatState.fromAuthor()) {
                boolean isDelete = false;
                if (chatState.isChangeHashtag()) {
                    isDelete = true;
                    if (update.getMessage() == null) {
                        isActive = false;
                    }
                }
                if (chatState.getLastMessage().getText().startsWith("http")) {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatState.getLastMessage().getChatId());
                    sendMessage.setText(chatState.getLastMessage().getText() + " from " + chatState.getAuthor());
                    execute(sendMessage);
                    isDelete = true;
                } else {
                    isActive = false;
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
            }
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

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botApiToken;
    }

    public class ChatState {
        private Long chatId;
        private Message lastMessage;
        private String username;
        private String name;
        private String hashtag;
        private String author;
        private boolean changeHashtag;

        public ChatState() {
            chatId = 0L;
            lastMessage = null;
            username = "unknown";
            name = "unknown";
            hashtag = "";
            author = "";
            changeHashtag = false;
        }

        public ChatState(Update update) {
            lastMessage = update.getMessage();
            chatId = lastMessage.getChatId();
            username = "unknown";
            name = "unknown";
            hashtag = "";
            author = "";
            changeHashtag = false;
        }

        public ChatState updateChatState(Update update) {
            chatId = update.getMessage().getChatId();

            lastMessage = update.getMessage();
            changeHashtag = false;
            Message message = update.getMessage();
            if (message == null) {
                message = update.getEditedMessage();
            }

            username = "unknown";
            name = "unknown";
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
            String description = "";
            if (message.getCaption() != null) {
                description = message.getCaption();
            }
            if (message.hasText()) {
                description = message.getText();
            }
            if (description.charAt(0) == '#' && description.length() > 1) {
                hashtag = description;
                if (message.getFrom().getUserName() != null) {
                    author = username;
                } else {
                    author = name;
                }
                changeHashtag = true;
                System.out.println("Hashtag change to " + hashtag + " from " + author);
            }
            return this;
        }

        public boolean fromAuthor() {
            return author.equals(username) || author.equals(name);
        }

        public Long getChatId() {
            return chatId;
        }

        public Message getLastMessage() {
            return lastMessage;
        }

        public String getUsername() {
            return username;
        }

        public String getName() {
            return name;
        }

        public String getHashtag() {
            return hashtag;
        }

        public String getAuthor() {
            return author;
        }

        public boolean isChangeHashtag() {
            return changeHashtag;
        }
    }
}


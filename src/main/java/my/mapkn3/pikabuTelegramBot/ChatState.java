package my.mapkn3.pikabuTelegramBot;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

public class ChatState {
    private Long chatId;
    private Message lastMessage;
    private String hashtag;
    private String author;
    private boolean changeHashtag;
    private Map<Integer, Message> userToLastMessage;

    public ChatState() {
        chatId = 0L;
        lastMessage = null;
        hashtag = "";
        author = "";
        changeHashtag = false;
        this.userToLastMessage = new HashMap<>();
    }

    public Message getLastMessageForUser(Integer userId) {
        return this.userToLastMessage.get(userId);
    }

    public Long getChatId() {
        return chatId;
    }

    public Message getLastMessage() {
        return lastMessage;
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

    public ChatState updateChatState(Update update) {
        changeHashtag = false;

        lastMessage = getMessageFromUpdate(update);
        chatId = lastMessage.getChatId();

        userToLastMessage.put(lastMessage.getFrom().getId(), lastMessage);

        String description = getTextContent();
        if (!description.trim().isEmpty() && description.charAt(0) == '#' && description.length() > 1 && !(description.toLowerCase().contains("#идеянедели") || description.toLowerCase().contains("#ин"))) {
            hashtag = description;
            if (getUsername().equals("unknown")) {
                author = getName();
            } else {
                author = getUsername();
            }
            changeHashtag = true;
            System.out.println("Hashtag change to " + hashtag + " from " + author);
        }
        return this;
    }

    public boolean fromAuthor() {
        return author.equals(getUsername()) || author.equals(getName());
    }

    public Message getMessageFromUpdate(Update update) {
        Message message = update.getMessage();
        if (message == null) {
            message = update.getEditedMessage();
        }
        return message;
    }

    public String getUsername() {
        String username = "unknown";
        if (lastMessage.getFrom().getUserName() != null) {
            username = "@" + lastMessage.getFrom().getUserName();
        }
        return username;
    }

    public String getName() {
        String name = "unknown";
        String lastName = lastMessage.getFrom().getLastName();
        String firstName = lastMessage.getFrom().getFirstName();
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
        return name;
    }

    public String getTextContent() {
        String textContent = "";
        if (lastMessage.getCaption() != null) {
            textContent = lastMessage.getCaption();
        }
        if (lastMessage.hasText()) {
            textContent = lastMessage.getText();
        }
        return textContent;
    }
}

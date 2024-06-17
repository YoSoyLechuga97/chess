package websocket.messages;

public class NotificationMessage extends ServerMessage{
    private final String message;
    public NotificationMessage(ServerMessageType type, String notification) {
        super(type);
        this.serverMessageType = ServerMessageType.NOTIFICATION;
        this.message = "[NOTIFICATION] " + notification;
    }

    public String getNotification() {
        return message;
    }
}

package websocket.messages;

public class NotificationMessage extends ServerMessage{
    private final String notification;
    public NotificationMessage(ServerMessageType type, String notification) {
        super(type);
        this.serverMessageType = ServerMessageType.NOTIFICATION;
        this.notification = "[NOTIFICATION] " + notification;
    }

    public String getNotification() {
        return notification;
    }
}

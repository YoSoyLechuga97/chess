package websocket.messages;

public class ErrorMessage extends ServerMessage{
    String error;
    public ErrorMessage(ServerMessageType type, String error) {
        super(type);
        this.serverMessageType = ServerMessageType.ERROR;
        this.error = "[ERROR] " + error;
    }
}

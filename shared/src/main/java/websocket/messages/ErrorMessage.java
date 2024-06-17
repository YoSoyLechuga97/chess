package websocket.messages;

public class ErrorMessage extends ServerMessage{
    private final String errorMessage;
    public String getErrorMessage() {
        return errorMessage;
    }
    public ErrorMessage(ServerMessageType type, String error) {
        super(type);
        this.serverMessageType = ServerMessageType.ERROR;
        this.errorMessage = "[ERROR] " + error;
    }
}

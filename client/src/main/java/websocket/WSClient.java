package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import facade.ChessDisplay;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WSClient extends Endpoint {
    public static void run(int port, boolean watchFromWhite, ChessGame game) throws Exception {
        var ws = new WSClient(port,watchFromWhite, game);
    }

    public Session session;

    public boolean watchFromWhite;
    public ChessGame game;
    public Gson gson = new Gson();

    public WSClient(int port, boolean watchFromWhite, ChessGame game) throws Exception {
        this.watchFromWhite = watchFromWhite;
        this.game = game;
        URI uri = new URI("ws://localhost:" + port + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        //Add Message handler and translate accordingly
        this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
            //Later we will deserialize it and use methods to translate it
            ServerMessage.ServerMessageType type = parseMessage(message);
            System.out.println();
            switch (type) {
                case NOTIFICATION:
                    readNotification(message);
                    break;
                case LOAD_GAME:
                    try {
                        loadGame(message);
                    } catch (InvalidMoveException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case ERROR:
                    readError(message);
                    break;
            }
        });
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    private ServerMessage.ServerMessageType parseMessage(String message) {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        String messageType = jsonObject.get("serverMessageType").getAsString();
        return switch (messageType) {
            case "NOTIFICATION" -> ServerMessage.ServerMessageType.NOTIFICATION;
            case "LOAD_GAME" -> ServerMessage.ServerMessageType.LOAD_GAME;
            case "ERROR" -> ServerMessage.ServerMessageType.ERROR;
            default -> null;
        };
    }

    public void readNotification(String serverMessage) {
        NotificationMessage message = gson.fromJson(serverMessage, NotificationMessage.class);
        System.out.println(message.getNotification());
    }

    public void readError(String message) {
        ErrorMessage error = gson.fromJson(message, ErrorMessage.class);
        System.out.println(error.getErrorMessage());
    }

    public void loadGame(String serverMessage) throws InvalidMoveException {
        LoadMessage message = gson.fromJson(serverMessage, LoadMessage.class);
        ChessDisplay chessDisplay = new ChessDisplay();
        chessDisplay.run(message.getGame(), watchFromWhite, null);
    }
}

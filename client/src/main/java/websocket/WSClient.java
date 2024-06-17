package websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WSClient extends Endpoint {
    public static void run(int port) throws Exception {
        var ws = new WSClient(port);
    }

    public Session session;

    public Gson gson = new Gson();

    public WSClient(int port) throws Exception {
        URI uri = new URI("ws://localhost:" + port + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        //Add Message handler and translate accordingly
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
                //Later we will deserialize it and use methods to translate it
                ServerMessage.ServerMessageType type = parseMessage(message);
                switch (type) {
                    case NOTIFICATION:
                        readNotification(message);
                        break;
                    case LOAD_GAME:
                        loadGame();
                }
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

    public void loadGame() {

    }
}

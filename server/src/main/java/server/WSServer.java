package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.server.NativeWebSocketServletContainerInitializer;
import spark.Spark;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;

import javax.websocket.server.ServerContainer;
import java.io.IOException;

@WebSocket
public class WSServer {
    public void run(int port) {
        Spark.webSocket("/ws", WSServer.class);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        //Deserialize message
        Gson gson = new Gson();
        UserGameCommand.CommandType type = parseCommand(message);
        //Save session with authToken
        //Save Session with gameID

        //Send to specified message handler
        switch (type) {
            case CONNECT:
                ConnectCommand connectCommand = gson.fromJson(message, ConnectCommand.class);
                connectMessage(session, connectCommand);
                break;
            case LEAVE:
                break;
        }
        session.getRemote().sendString("WebSocket response: " + message);
    }

    public UserGameCommand.CommandType parseCommand(String message) {
        Gson gson = new Gson();
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        String commandType = jsonObject.get("commandType").getAsString();
        return switch (commandType) {
            case "CONNECT" -> UserGameCommand.CommandType.CONNECT;
            case "MAKE_MOVE" -> UserGameCommand.CommandType.MAKE_MOVE;
            case "LEAVE" -> UserGameCommand.CommandType.LEAVE;
            case "RESIGN" -> UserGameCommand.CommandType.RESIGN;
            default -> null;
        };
    }

    public void connectMessage(Session session, ConnectCommand connectCommand) throws IOException {
        int gameID = connectCommand.getGameID();
        session.getRemote().sendString("This was a user connecting to game: " + gameID);
    }
}

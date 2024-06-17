package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.Spark;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class WSServer {

    private final Map<Integer, ArrayList<Session>> sessionsFromID = new HashMap<>();
    private final Map<Session, String> authFromSession = new HashMap<>();
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
                addToken(session, connectCommand.getAuthString());
                addSession(connectCommand.getGameID(), session);
                connectMessage(session, connectCommand);
                if (connectCommand.getJoined()) {
                    sendNotification(session, connectCommand.getGameID(), getUsername(connectCommand.getAuthString()) + " has joined the game as " + connectCommand.getColor() + "!");
                } else {
                    sendNotification(session, connectCommand.getGameID(), getUsername(connectCommand.getAuthString()) + "has joined the game as an observer!");
                }
                break;
            case LEAVE:
                break;
        }
        session.getRemote().sendString("WebSocket response: " + message);
    }

    public void addSession(int gameID, Session session) {
        sessionsFromID.computeIfAbsent(gameID, k -> new ArrayList<>()).add(session);
    }

    public void addToken(Session session, String authToken) throws IOException {
        try {
            authFromSession.put(session, authToken);
        } catch (Exception e) {
            session.getRemote().sendString("Failed to add to Map" + e.getMessage());
        }
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

    public String getUsername(String authToken) throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDAO();
        return authDAO.getUser(authToken);
    }

    public void connectMessage(Session session, ConnectCommand connectCommand) throws IOException {
        int gameID = connectCommand.getGameID();
        session.getRemote().sendString("This was a user connecting to game: " + gameID);
    }

    public void sendNotification(Session session, int gameID, String message) throws IOException {
        //Find all users that need to be sent the information
        for (Session userSession : sessionsFromID.getOrDefault(gameID, new ArrayList<>())) {
            if (!authFromSession.get(userSession).equals(authFromSession.get(session))) {
                userSession.getRemote().sendString("[NOTIFICATION] " + message);
            }
        }
    }
}

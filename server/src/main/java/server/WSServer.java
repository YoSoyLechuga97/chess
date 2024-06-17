package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.Spark;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class WSServer {

    private final Map<Integer, ArrayList<Session>> sessionsFromID = new HashMap<>();
    private final Map<Session, String> authFromSession = new HashMap<>();
    private final Gson gson = new Gson();
    public void run(int port) {
        Spark.webSocket("/ws", WSServer.class);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        //Deserialize message
        //Gson gson = new Gson();
        //session.getRemote().sendString("WebSocket response: " + message);
        UserGameCommand.CommandType type = parseCommand(message);
        //Save session with authToken
        //Save Session with gameID

        //Send to specified message handler
        switch (type) {
            case CONNECT:
                ConnectCommand connectCommand = gson.fromJson(message, ConnectCommand.class);
                addToken(session, connectCommand.getAuthString());
                addSession(connectCommand.getGameID(), session);
                sendLoadGame(session, connectCommand.getGame());
                if (connectCommand.getJoined()) {
                    sendNotification(session, connectCommand.getGameID(), getUsername(connectCommand.getAuthString()) + " has joined the game as " + connectCommand.getColor() + "!");
                } else {
                    sendNotification(session, connectCommand.getGameID(), getUsername(connectCommand.getAuthString()) + " has joined the game as an observer!");
                }
                break;
            case LEAVE:
                LeaveCommand leaveCommand = gson.fromJson(message, LeaveCommand.class);
                sendNotification(session, leaveCommand.getGameID(), getUsername(leaveCommand.getAuthString()) + " has left the game");
                //Remove root session
                removeToken(session, leaveCommand.getAuthString());
                //Remove root from list of sessions
                removeSessionFromGame(leaveCommand.getGameID(), session);
                //Remove player from game
                if (leaveCommand.getIsPlayer()) {
                    removePlayerFromGame(leaveCommand.getGameID(), leaveCommand.getIsWhite());
                }
                break;
            case RESIGN:

                break;
        }
        //session.getRemote().sendString("WebSocket response: " + message);
    }

    public void addSession(int gameID, Session session) {
        sessionsFromID.computeIfAbsent(gameID, k -> new ArrayList<>()).add(session);
    }

    public void removeSessionFromGame(int gameID, Session session) {
        ArrayList<Session> gameSessions = sessionsFromID.get(gameID);
        gameSessions.removeIf(session::equals);
    }

    public void addToken(Session session, String authToken) throws IOException {
        try {
            authFromSession.put(session, authToken);
        } catch (Exception e) {
            session.getRemote().sendString("Failed to add to Map" + e.getMessage());
        }
    }

    public void removeToken(Session session, String authToken) {
        authFromSession.remove(session);
    }

    public void removePlayerFromGame(int gameID, boolean isWhite) throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        GameData oldGame = gameDAO.getGame(gameID);
        GameData newGame;
        if (isWhite) {
            newGame = new GameData(oldGame.gameID(), null, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        } else {
            newGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), null, oldGame.gameName(), oldGame.game());
        }

        gameDAO.updateGame(newGame);

    }

    public UserGameCommand.CommandType parseCommand(String message) {
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

    public void sendLoadGame(Session session, ChessGame game) throws IOException {
        LoadMessage message = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        String json = gson.toJson(message);
        session.getRemote().sendString(json);
    }

    public void sendNotification(Session session, int gameID, String message) throws IOException {
        //Find all users that need to be sent the information
        for (Session userSession : sessionsFromID.getOrDefault(gameID, new ArrayList<>())) {
            if (!authFromSession.get(userSession).equals(authFromSession.get(session))) {
                NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                String json = gson.toJson(notificationMessage);
                userSession.getRemote().sendString(json);
            }
        }
    }
}

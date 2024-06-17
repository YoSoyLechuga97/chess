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
import websocket.commands.ResignCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
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
        try {
            //Deserialize message
            UserGameCommand.CommandType type = parseCommand(message);
            //Send to specified message handler
            switch (type) {
                case CONNECT:
                    ConnectCommand connectCommand = gson.fromJson(message, ConnectCommand.class);
                    if (!verifyToken(connectCommand.getAuthString())) {
                        throw new Exception("Not authorized token");
                    }
                    addToken(session, connectCommand.getAuthString());
                    addSession(connectCommand.getGameID(), session);
                    sendLoadGame(session, connectCommand.getGameID());
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
                    //Set game to finished
                    ResignCommand resignCommand = gson.fromJson(message, ResignCommand.class);
                    resignFromGame(resignCommand.getGameID());
                    notifyAllClients(resignCommand.getGameID(), getUsername(resignCommand.getAuthString()) + " has resigned from the game-GAME OVER");
                    break;
            }
        } catch (Exception e) {
            sendErrorMessage(session, e);
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

    public void sendErrorMessage(Session session, Exception e) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
        String jsonError = gson.toJson(errorMessage);
        session.getRemote().sendString(jsonError);
    }

    public void sendLoadGame(Session session, int gameID) throws IOException, DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        ChessGame game = gameDAO.getGame(gameID).game();
        LoadMessage message = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        String json = gson.toJson(message);
        session.getRemote().sendString(json);
    }

    public boolean verifyToken(String token) throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDAO();
        return authDAO.getAuth(token);
    }

    public void resignFromGame(int gameID) throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        GameData game = gameDAO.getGame(gameID);
        game.game().setTeamTurn(null);
        GameData finishedGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        gameDAO.updateGame(finishedGame);
    }

    public void notifyAllClients(int gameID, String message) throws IOException {
        for (Session userSession : sessionsFromID.getOrDefault(gameID, new ArrayList<>())) {
            NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            String json = gson.toJson(notificationMessage);
            userSession.getRemote().sendString(json);
        }
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

package server;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.Spark;
import websocket.commands.*;
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
                    //Remove player from game
                    removePlayerFromGame(leaveCommand);
                    sendNotification(session, leaveCommand.getGameID(), getUsername(leaveCommand.getAuthString()) + " has left the game");
                    //Remove root session
                    removeToken(session, leaveCommand.getAuthString());
                    //Remove root from list of sessions
                    removeSessionFromGame(leaveCommand.getGameID(), session);
                    break;
                case MAKE_MOVE:
                    MakeMoveCommand makeMoveCommand = gson.fromJson(message, MakeMoveCommand.class);
                    if (!verifyToken(makeMoveCommand.getAuthString())) {
                        throw new Exception("Not authorized token");
                    }
                    if (!verifyTurn(makeMoveCommand) || !verifyControl(makeMoveCommand)) {
                        throw new Exception("It's not your turn");
                    }
                    makeMove(makeMoveCommand);
                    sendLoadGameToAll(makeMoveCommand.getGameID());
                    sendNotification(session, makeMoveCommand.getGameID(), getUsername(makeMoveCommand.getAuthString()) + " moved from {" + makeMoveCommand.getMove().getStartPosition().getRow() + "," + makeMoveCommand.getMove().getStartPosition().getColumn() + "} to {" + makeMoveCommand.getMove().getEndPosition().getRow() + "," + makeMoveCommand.getMove().getEndPosition().getColumn() + "}");
                    checkGameStatus(makeMoveCommand);
                    break;
                case RESIGN:
                    //Set game to finished
                    ResignCommand resignCommand = gson.fromJson(message, ResignCommand.class);
                    if (!verifyInPlay(resignCommand)) {
                        throw new Exception("Game has already ended");
                    }
                    if (!verifyPlayer(resignCommand)) {
                        throw new Exception("Only players can resign");
                    }
                    resignFromGame(resignCommand.getGameID());
                    notifyAllClients(resignCommand.getGameID(), getUsername(resignCommand.getAuthString()) + " has resigned from the game-GAME OVER");
                    break;
            }
        } catch (Exception e) {
            sendErrorMessage(session, e);
        }
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

    public void removePlayerFromGame(LeaveCommand leaveCommand) throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        GameData oldGame = gameDAO.getGame(leaveCommand.getGameID());
        GameData newGame;
        if (getUsername(leaveCommand.getAuthString()).equals(oldGame.whiteUsername())) {
            newGame = new GameData(oldGame.gameID(), null, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        } else if (getUsername(leaveCommand.getAuthString()).equals(oldGame.blackUsername())){
            newGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), null, oldGame.gameName(), oldGame.game());
        } else {
            newGame = oldGame;
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

    public void makeMove(MakeMoveCommand makeMoveCommand) throws DataAccessException, InvalidMoveException {
        GameDAO gameDAO = new SQLGameDAO();
        GameData gameData = gameDAO.getGame(makeMoveCommand.getGameID());
        ChessGame game = gameData.game();
        game.makeMove(makeMoveCommand.getMove());
        gameDAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
    }
    public void checkGameStatus(MakeMoveCommand makeMoveCommand) throws DataAccessException, IOException {
        GameDAO gameDAO = new SQLGameDAO();
        GameData gameData = gameDAO.getGame(makeMoveCommand.getGameID());
        ChessGame game = gameData.game();
        ChessGame.TeamColor teamColor;
        if (game.getBoard().getPiece(makeMoveCommand.getMove().getEndPosition()).getTeamColor() == ChessGame.TeamColor.WHITE) {
            teamColor = ChessGame.TeamColor.BLACK;
        } else {
            teamColor = ChessGame.TeamColor.WHITE;
        }
        if (game.isInCheck(teamColor)) {
            notifyAllClients(makeMoveCommand.getGameID(), teamColor + " is in check!");
        } else if (game.isInCheckmate(teamColor)) {
            notifyAllClients(makeMoveCommand.getGameID(), teamColor + " is in checkmate! GAME OVER");
        }
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

    public boolean verifyInPlay(ResignCommand resignCommand) throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        GameData gameData = gameDAO.getGame(resignCommand.getGameID());
        ChessGame game = gameData.game();
        return game.getTeamTurn() != ChessGame.TeamColor.NONE;
    }

    public boolean verifyTurn(MakeMoveCommand makeMove) throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        GameData gameData = gameDAO.getGame(makeMove.getGameID());
        ChessGame game = gameData.game();
        ChessGame.TeamColor turnColor = game.getTeamTurn();
        ChessGame.TeamColor colorTryingMove = game.getBoard().getPiece(makeMove.getMove().getStartPosition()).getTeamColor();
        return turnColor == colorTryingMove;
    }

    public boolean verifyControl(MakeMoveCommand moveMade) throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        GameData gameData = gameDAO.getGame(moveMade.getGameID());
        String username = authDAO.getUser(moveMade.getAuthString());
        ChessGame game = gameData.game();
        ChessGame.TeamColor turnColor = game.getTeamTurn();
        if (turnColor == ChessGame.TeamColor.WHITE) {
            return username.equals(gameData.whiteUsername());
        } else {
            return username.equals(gameData.blackUsername());
        }
    }

    public boolean verifyPlayer(ResignCommand resignCommand) throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        GameData gameData = gameDAO.getGame(resignCommand.getGameID());
        String username = authDAO.getUser(resignCommand.getAuthString());
        return (gameData.whiteUsername().equals(username) || gameData.blackUsername().equals(username));
    }

    public void resignFromGame(int gameID) throws DataAccessException {
        GameDAO gameDAO = new SQLGameDAO();
        GameData game = gameDAO.getGame(gameID);
        game.game().setTeamTurn(ChessGame.TeamColor.NONE);
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

    public void sendLoadGameToAll(int gameID) throws DataAccessException, IOException {
        GameDAO gameDAO = new SQLGameDAO();
        ChessGame game = gameDAO.getGame(gameID).game();
        for (Session userSession : sessionsFromID.getOrDefault(gameID, new ArrayList<>())) {
            LoadMessage loadMessage = new LoadMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            String json = gson.toJson(loadMessage);
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

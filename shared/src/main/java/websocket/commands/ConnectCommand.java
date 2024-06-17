package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {

    private final int gameID;
    private final boolean joined;
    private final String color;

    private final ChessGame game;

    public ChessGame getGame() {
        return game;
    }

    public int getGameID() {
        return gameID;
    }

    public boolean getJoined() {
        return joined;
    }

    public String getColor() {
        return color;
    }

    public ConnectCommand(String authToken, int gameID, boolean joined, String color, ChessGame game) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.CONNECT;
        this.joined = joined;
        this.color = color;
        this.game = game;
    }
}

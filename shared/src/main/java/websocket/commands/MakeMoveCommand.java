package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    private final ChessMove move;
    private final int gameID;
    public ChessMove getMove() {
        return move;
    }
    public int getGameID() {
        return gameID;
    }
    public MakeMoveCommand(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.move = move;
        this.gameID = gameID;
    }
}

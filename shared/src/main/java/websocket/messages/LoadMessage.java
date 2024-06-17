package websocket.messages;

import chess.ChessGame;

public class LoadMessage extends ServerMessage{
    private final ChessGame game;

    public ChessGame getGame() {
        return game;
    }

    public LoadMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game = game;
    }
}

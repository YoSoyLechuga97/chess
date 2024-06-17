package websocket.commands;

import websocket.commands.UserGameCommand;

public class ConnectCommand extends UserGameCommand {

    private final int gameID;

    public int getGameID() {
        return gameID;
    }

    public ConnectCommand(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.CONNECT;
    }
}

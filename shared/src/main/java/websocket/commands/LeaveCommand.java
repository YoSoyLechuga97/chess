package websocket.commands;

public class LeaveCommand extends UserGameCommand{
    private final boolean isPlayer;
    private final int gameID;
    private final boolean isWhite;

    public int getGameID() {
        return gameID;
    }

    public boolean getIsPlayer() {
        return isPlayer;
    }
    public boolean getIsWhite() {
        return isWhite;
    }
    public LeaveCommand(String authToken, boolean isPlayer, boolean isWhite, int gameID) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
        this.isPlayer = isPlayer;
        this.isWhite = isWhite;
        this.gameID = gameID;
    }
}

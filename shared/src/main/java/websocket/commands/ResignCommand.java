package websocket.commands;

public class ResignCommand extends UserGameCommand{
    private final int gameID;

    public int getGameID() {
        return gameID;
    }
    public ResignCommand(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.RESIGN;
        this.gameID = gameID;
    }
}

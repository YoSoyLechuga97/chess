package websocket.commands;

public class LeaveCommand extends UserGameCommand{
    private boolean isPlayer;

    public boolean getIsPlayer() {
        return isPlayer;
    }
    public LeaveCommand(String authToken, boolean isPlayer) {
        super(authToken);
        this.isPlayer = isPlayer;
    }
}

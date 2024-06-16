package facade;

import chess.ChessGame;
import chess.InvalidMoveException;
import model.AuthData;

public class InGame {
    AuthData terminalAuthData = null;
    ServerFacade serverFacade;
    ChessDisplay chessDisplay = new ChessDisplay();

    boolean watchFromWhite;

    public void playGame() {

    }

    public void observeGame(AuthData terminalAuthData, ChessGame game) throws InvalidMoveException {
        this.terminalAuthData = terminalAuthData;
        watchFromWhite = true;
        chessDisplay.run(game, watchFromWhite);
    }
}

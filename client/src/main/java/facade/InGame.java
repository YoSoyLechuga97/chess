package facade;

import chess.ChessGame;
import chess.InvalidMoveException;
import model.AuthData;

import static facade.TerminalUI.readInput;

public class InGame {
    AuthData terminalAuthData = null;
    ServerFacade serverFacade;
    ChessDisplay chessDisplay = new ChessDisplay();
    boolean leave = false;

    boolean watchFromWhite;

    public void playGame(AuthData terminalAuthData, ChessGame game, boolean watchFromWhite) throws Exception{
        this.terminalAuthData = terminalAuthData;
        this.watchFromWhite = watchFromWhite;
        chessDisplay.run(game, watchFromWhite);
        while (!leave) {
            System.out.print("[PLAYING] >>> ");
            String[] userInput = readInput();
            switch (userInput[0]) {
                case "help":
                    System.out.println("redraw - the chess board\nleave - the game\nmove - a chess piece\nresign - from the game\nhighlight - a piece to see potential moves\nhelp - with possible commands\n");
                    break;
                case "leave":
                    System.out.println("Leaving Game\n");
                    leave = true;
                    break;
                case "redraw":
                    chessDisplay.run(game, watchFromWhite);
                    break;
                case "move":
                    System.out.println("TODO:MAKEMOVE");
                    break;
                case "resign":
                    System.out.println("TODO:RESIGN");
                    break;
                case "highlight":
                    System.out.println("TODO:HIGHLIGHT");
                default:
                    System.out.println("Unrecognized command");
                    break;
            }
        }
    }

    public void observeGame(AuthData terminalAuthData, ChessGame game) throws InvalidMoveException {
        this.terminalAuthData = terminalAuthData;
        watchFromWhite = true;
        chessDisplay.run(game, watchFromWhite);
        while (!leave) {
            System.out.print("[OBSERVING] >>> ");
            String[] userInput = readInput();
            switch (userInput[0]) {
                case "help":
                    System.out.println("redraw - the chess board\nleave - the game\nhelp - with possible commands\n");
                    break;
                case "leave":
                    System.out.println("Leaving Game\n");
                    leave = true;
                    break;
                case "redraw":
                    chessDisplay.run(game, watchFromWhite);
                    break;
                default:
                    System.out.println("Unrecognized command");
                    break;
            }
        }
    }
}
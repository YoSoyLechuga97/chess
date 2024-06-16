package facade;

import chess.ChessGame;
import chess.InvalidMoveException;
import model.AuthData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    System.out.println("redraw - the chess board\nleave - the game\nmove - a chess piece\nresign - from the game\nhighlight <POSITION> - to see potential moves\nhelp - with possible commands\n");
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
                    if (userInput.length == 2) { //Make sure the command is formatted correctly
                        String patternString = "([a-hA-H])([1-8])";
                        Pattern pattern = Pattern.compile(patternString);
                        Matcher matcher = pattern.matcher(userInput[1]);
                        if (matcher.matches()) {
                            //Determine there is a piece at that position on the board
                            int col = matcher.group(1).charAt(0) - 'a' + 1;
                            int row = Integer.parseInt(matcher.group(2));
                            //if(game.getBoard().getPiece())
                            System.out.println("Your row is: " + row + " and your col is " + col);
                            //Create an array of chessPositions for all possible moves


                            System.out.println("You input <POSITION> correctly!");
                        } else {
                            System.out.println("<POSITION> must be typed letter then number with no spaces and exist on the board, like 'a3'");
                        }
                        break;
                    }
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

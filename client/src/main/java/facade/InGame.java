package facade;

import chess.*;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import websocket.commands.ConnectCommand;
import websocket.WSClient;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static facade.TerminalUI.readInput;

public class InGame {
    Gson gson = new Gson();
    //Start the Websocket
    WSClient websocket;
    AuthData terminalAuthData = null;
    ServerFacade serverFacade;
    ChessDisplay chessDisplay = new ChessDisplay();
    boolean leave = false;

    boolean watchFromWhite;

    public void playGame(AuthData terminalAuthData, ChessGame game, boolean watchFromWhite, int port, int gameID, String color) throws Exception{
        this.terminalAuthData = terminalAuthData;
        this.watchFromWhite = watchFromWhite;
        this.serverFacade = new ServerFacade(port);
        //Connect Websocket
        websocket = new WSClient(port, watchFromWhite, game);
        //NOTIFY that the player has joined, need username and color
        ConnectCommand connectCommand = new ConnectCommand(terminalAuthData.authToken(), gameID, true, color, game);
        String json = gson.toJson(connectCommand);
        websocket.send(json);
        while (!leave) {
            System.out.print("[PLAYING] >>> ");
            String[] userInput = readInput();
            switch (userInput[0]) {
                case "help":
                    System.out.println("redraw - the chess board\nleave - the game\nmove - a chess piece\nresign - from the game\nhighlight <POSITION> - to see potential moves\nhelp - with possible commands\n");
                    break;
                case "leave":
                    System.out.println("Leaving Game\n");
                    //NOTIFY THAT YOU LEFT GAME, need username
                    LeaveCommand leaveCommand = new LeaveCommand(terminalAuthData.authToken(), true, watchFromWhite, gameID);
                    String leaveJson = gson.toJson(leaveCommand);
                    websocket.send(leaveJson);
                    leave = true;
                    break;
                case "redraw":
                    redraw(gameID);
                    break;
                case "move":
                    //Parse user move
                    if (userInput.length == 2) {
                        String patternString = "^([a-hA-H])([1-8])([a-hA-H])([1-8])(ROOK|BISHOP|KNIGHT|QUEEN)?$";
                        Pattern pattern = Pattern.compile(patternString);
                        Matcher matcher = pattern.matcher(userInput[1]);
                        if (matcher.matches()) {
                            //Create move
                            ChessMove move = createMove(matcher);
                            //NOTIFY THAT YOU MADE A MOVE, need username, description of move
                            MakeMoveCommand makeMoveCommand = new MakeMoveCommand(terminalAuthData.authToken(), gameID, move);
                            String jsonMove = gson.toJson(makeMoveCommand);
                            websocket.send(jsonMove);
                        } else {
                            System.out.println("Please type in your move correctly (no spaces), for example 'a3a4', if you are promoting a piece please also include the promotion piece in ALL CAPS in the end");
                        }
                        break;
                    }
                case "resign":
                    //NOTIFY THAT YOU RESIGNED THE GAME, need username
                    ResignCommand resignCommand = new ResignCommand(terminalAuthData.authToken(), gameID);
                    String resignJson = gson.toJson(resignCommand);
                    websocket.send(resignJson);
                    break;
                case "highlight":
                    if (userInput.length == 2) { //Make sure the command is formatted correctly
                        ArrayList<ChessPosition> squaresToHighlight = highlight(userInput, game);
                        if (!squaresToHighlight.isEmpty()) {
                            chessDisplay.run(game, watchFromWhite, squaresToHighlight);
                        }
                        break;
                    }
                default:
                    System.out.println("Unrecognized command");
                    break;
            }
        }
    }

    public void observeGame(AuthData terminalAuthData, ChessGame game, int gameID) throws InvalidMoveException, URISyntaxException, IOException {
        this.terminalAuthData = terminalAuthData;
        watchFromWhite = true;
        chessDisplay.run(game, watchFromWhite, null);
        //NOTIFY that you have joined as an observer, need username
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
                    //NOTIFY PLAYERS THAT YOU'VE LEFT, need authToken
                    //DISCONNECT FROM WS
                    break;
                case "redraw":
                    redraw(gameID);
                    break;
                default:
                    System.out.println("Unrecognized command");
                    break;
            }
        }
    }

    public ChessMove createMove(Matcher matcher) {
        int startCol = matcher.group(1).charAt(0) - 'a' + 1;
        int startRow = Integer.parseInt(matcher.group(2));
        int endCol = matcher.group(3).charAt(0) - 'a' + 1;
        int endRow = Integer.parseInt(matcher.group(4));
        String piece = matcher.group(5);
        ChessPosition startPos = new ChessPosition(startRow, startCol);
        ChessPosition endPos = new ChessPosition(endRow, endCol);
        if (piece == null) {
            return new ChessMove(startPos, endPos, null);
        }
        else {
            return new ChessMove(startPos, endPos, pieceType(piece));
        }
    }

    public ChessPiece.PieceType pieceType(String piece) {
        return switch (piece) {
            case "BISHOP" -> ChessPiece.PieceType.BISHOP;
            case "ROOK" -> ChessPiece.PieceType.ROOK;
            case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
            case "QUEEN" -> ChessPiece.PieceType.QUEEN;
            default -> null;
        };
    }

    public void redraw(int gameID) throws URISyntaxException, IOException, InvalidMoveException {
        ArrayList<GameData> allGames = serverFacade.listGames(terminalAuthData);
        for (GameData gameData : allGames) {
            if (gameData.gameID() == gameID) {
                chessDisplay.run(gameData.game(), watchFromWhite, null);
            }
        }
    }

    public ArrayList<ChessPosition> highlight(String[] userInput, ChessGame game) {
        String patternString = "([a-hA-H])([1-8])";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(userInput[1]);
        ArrayList<ChessPosition> squaresToHighlight = new ArrayList<>();
        if (matcher.matches()) {
            //Determine there is a piece at that position on the board
            int col = matcher.group(1).charAt(0) - 'a' + 1;
            int row = Integer.parseInt(matcher.group(2));
            ChessPosition highlightPosition = new ChessPosition(row, col);
            if (game.getBoard().getPiece(highlightPosition) != null) {
                //Obtain a list of all possible moves
                Collection<ChessMove> validMoves = game.validMoves(highlightPosition);
                if (!validMoves.isEmpty()) {
                    //Create an array list of positions as numbers
                    for (ChessMove move : validMoves) {
                        int currRow = move.getEndPosition().getRow();
                        int currCol = move.getEndPosition().getColumn();
                        ChessPosition nextPos = new ChessPosition(currRow, currCol);
                        squaresToHighlight.add(nextPos);
                    }
                    //Display the highlighted board
//                    chessDisplay.run(game, watchFromWhite, squaresToHighlight);
                    return squaresToHighlight;
                } else {
                    System.out.println("This piece has no valid moves");
                    return new ArrayList<>();
                }
            } else {
                System.out.println("There is no chessPiece at " + userInput[1]);
                return new ArrayList<>();
            }
        } else {
            System.out.println("<POSITION> must be typed letter then number with no spaces and exist on the board, like 'a3'");
            return new ArrayList<>();
        }
    }
}

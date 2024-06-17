package facade;

import chess.*;
import facade.ServerFacade;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TerminalUI {
    AuthData terminalAuthData = null;
    ServerFacade serverFacade;
    InGame inGame;
    ChessDisplay chessDisplay = new ChessDisplay();
    Map<Integer, Integer> gameList = new HashMap<>();
    Map<Integer, ChessGame> gameFromID = new HashMap<>();
    boolean quit = false;
    public void run(int port) throws Exception {
        this.serverFacade = new ServerFacade(port);
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        while (!quit) {
            preLogin();
            if (!quit) {
                postLogin();
            }
        }
    }

    public void preLogin() throws Exception {
        while (terminalAuthData == null) {
            System.out.print("[LOGGED_OUT] >>> ");
            String[] userInput = readInput();
            switch (userInput[0]) {
                //Help
                case "help":
                    System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account\nlogin <USERNAME> <PASSWORD> - to play chess\nquit - playing chess\nhelp - with possible commands\n");
                    break;
                //Quit
                case "quit":
                    System.out.println("Thank you for playing!");
                    quit = true;
                    return;
                //Login
                case "login":
                    if (userInput.length == 3) {
                        terminalAuthData = serverFacade.login(userInput[1], userInput[2]);
                        if (terminalAuthData != null) { System.out.println("Logged in as " + terminalAuthData.username()); }
                        break;
                    }
                    //Register
                case "register":
                    if (userInput.length == 4) {
                        terminalAuthData = serverFacade.register(userInput[1], userInput[2], userInput[3]);
                        if (terminalAuthData != null) { System.out.println("Logged in as " + terminalAuthData.username()); }
                        break;
                    }
                    //Unknown Command
                default:
                    System.out.println("Unrecognized command");
                    break;
            }
        }
    }
    public void postLogin() throws Exception {
        //Postlogin UI;
        while (terminalAuthData != null) {
            System.out.print("[LOGGED_IN] >>> ");
            String[] userInput = readInput();
            switch (userInput[0]) {
                //Help
                case "help":
                    System.out.println("create <NAME> - a game\nlist - games\njoin <ID> [WHITE|BLACK] - a game\nobserve <ID> - a game\nlogout - when you are done\nquit - playing chess\nhelp - with possible commands\n");
                    break;
                //Create
                case "create":
                    if (userInput.length == 2) {
                        serverFacade.createGame(terminalAuthData, userInput[1]);
                        break;
                    }
                    //List
                case "list":
                    ArrayList<GameData> allGames = serverFacade.listGames(terminalAuthData);
                    int i = 1;
                    for (GameData game : allGames) {
                        System.out.println(i + ". " + game.gameName() + " W: " + game.whiteUsername() + " B: " + game.blackUsername());
                        gameList.put(i, game.gameID());
                        gameFromID.put(game.gameID(), game.game());
                        i++;
                    }
                    System.out.println("\n");
                    break;
                //Join
                case "join":
                    if (userInput.length == 3) {
                        int userNumber;
                        try {
                            userNumber = Integer.parseInt(userInput[1]);
                        } catch (NumberFormatException e) {
                            System.out.println("Not a valid game number");
                            break;
                        }
                        int gameToJoin = gameList.get(userNumber);
                        serverFacade.joinGame(terminalAuthData, userInput[2], gameToJoin);
                        boolean isWhite = userInput[2].equals("WHITE");
                        inGame = new InGame();
                        inGame.playGame(terminalAuthData, gameFromID.get(gameToJoin), isWhite, serverFacade.port);
                        // chessDisplay.run(gameFromID.get(gameToJoin));
                        break;
                    }
                    //Observe
                case "observe":
                    if (userInput.length == 2) {
                        int userNumber;
                        try {
                            userNumber = Integer.parseInt(userInput[1]);
                        } catch (NumberFormatException e) {
                            System.out.println("Not a valid game number");
                            break;
                        }
                        //Send to InGame
                        int gameToWatch = gameList.get(userNumber);
                        inGame = new InGame();
                        inGame.observeGame(terminalAuthData, gameFromID.get(gameToWatch));
                        //chessDisplay.run(gameFromID.get(gameToWatch));
                        break;
                    }
                    //Logout
                case "logout":
                    terminalAuthData = serverFacade.logout(terminalAuthData);
                    break;
                //Quit
                case "quit":
                    terminalAuthData = serverFacade.logout(terminalAuthData);
                    System.out.println("Thank you for playing!");
                    quit = true;
                //Unknown Command
                default:
                    if(!quit) {
                        System.out.println("Unrecognized command");
                    }
                    break;
            }
        }
    }

    public static String[] readInput() {
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        return userInput.split(" ");
    }
}
import chess.*;
import dataaccess.DataAccessException;
import model.AuthData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws DataAccessException, URISyntaxException, IOException {
        AuthData terminalAuthData = null;
        Scanner scanner = new Scanner(System.in);
        ServerFacade serverFacade = new ServerFacade();
        boolean quit = false;
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        while (!quit) {
            //Prelogin UI
            while (terminalAuthData == null) {
                System.out.print("[LOGGED_OUT] >>> ");
                String[] userInput = readInput();
                switch (userInput[0]) {
                    //Help
                    case "help":
                        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                        System.out.println("login <USERNAME> <PASSWORD> - to play chess");
                        System.out.println("quit - playing chess");
                        System.out.println("help - with possible commands");
                        System.out.println("\n");
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
                            if (terminalAuthData != null) {
                                System.out.println("Logged in as " + terminalAuthData.username());
                            }
                            break;
                        }
                        //Register
                    case "register":
                        if (userInput.length == 4) {
                            terminalAuthData = serverFacade.register(userInput[1], userInput[2], userInput[3]);
                            if (terminalAuthData != null) {
                                System.out.println("Logged in as " + terminalAuthData.username());
                            }
                            break;
                        }
                        //Unknown Command
                    default:
                        System.out.println("Unrecognized command");
                        break;
                }
            }

            //Postlogin UI
            while (terminalAuthData != null) {
                System.out.print("[LOGGED_IN] >>> ");
                String[] userInput = readInput();
                switch (userInput[0]) {
                    //Help
                    case "help":
                        System.out.println("create <NAME> - a game");
                        System.out.println("list - games");
                        System.out.println("observe <ID> - a game");
                        System.out.println("logout - when you are done");
                        System.out.println("quit - playing chess");
                        System.out.println("help - with possible commands");
                        System.out.println("\n");
                        break;
                    //Create

                    //List

                    //Join

                    //Observe

                    //Logout
                    case "logout":
                        terminalAuthData = serverFacade.logout(terminalAuthData);
                        break;
                    //Quit
                    case "quit":
                        System.out.println("Thank you for playing!");
                        quit = true;
                        return;
                    //Unknown Command
                    default:
                        System.out.println("Unrecognized command");
                        break;
                }
            }
        }
    }

    public static String[] readInput() {
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        String[] args = userInput.split(" ");
        return args;
    }
}
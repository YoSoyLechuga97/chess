import chess.*;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        AuthData terminalAuthData = null;
        Scanner scanner = new Scanner(System.in);
        try {
            ServerFacade serverFacade = new ServerFacade();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

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
                    return;
                //Login
                case "login":

                //Register

                //Unknown Command

            }
        }

        //Postlogin UI
    }

    public static String[] readInput() {
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        String[] args = userInput.split(" ");
        return args;
    }
}
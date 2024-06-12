package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public class GameService {
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();

    public ArrayList<GameData> listGames(AuthData userToken) throws DataAccessException {
        //Verify user can access file
        if (!verifyToken(userToken)) { //Does not have authentication
            System.out.println("You do not have access");
            return null;
        }
        //List all games
        return gameDAO.listGames();
    }
    public int createGame(AuthData userToken, String newGameName) throws DataAccessException {
        //Verify token
        if (!verifyToken(userToken)) { //Does not have authentication
            System.out.println("You do not have access");
            return -1;
        }
        //Check to see if game name is already in database
        ArrayList<GameData> allGames = gameDAO.listGames();
        for (GameData game : allGames) {
            if (game.gameName().equals(newGameName)) {
                System.out.println("A game with this name already exists :/");
                return -2;
            }
        }
        //Create game
        return gameDAO.createGame(userToken.authToken(), newGameName);
    }
    public boolean joinGame(AuthData userToken, String playerColor, int gameID) throws DataAccessException {
        //Verify
        if (!verifyToken(userToken)) { //Does not have authentication
            System.out.println("You do not have access");
            return false;
        }
        //Determine if game exists
        GameData oldGame = gameDAO.getGame(gameID);
        if (oldGame == null) {
            System.out.println("Game does not exist");
            return false;
        }
        //Extract game Data
        String gameName = oldGame.gameName();
        String whitePlayer = oldGame.whiteusername();
        String blackPlayer = oldGame.blackUsername();
        ChessGame game = oldGame.game();

        //Make sure that player color isn't already taken
        if (playerColor.equals("WHITE")) {
            if (!whitePlayer.equals("NO USER")) {
                System.out.println("This color is already taken");
                return false;
            } else {
                whitePlayer = userToken.username();
            }
        } else {
            if (!blackPlayer.equals("NO USER")) {
                System.out.println("This color is already taken");
                return false;
            } else {
                blackPlayer = userToken.username();
            }
        }

        //Create updated game
        GameData updatedGame = new GameData(gameID, whitePlayer, blackPlayer, gameName, game);
        gameDAO.updateGame(updatedGame);
        return true;
    }
    public boolean verifyToken(AuthData userToken) throws DataAccessException {
        return authDAO.getAuth(userToken.authToken());
    }
}
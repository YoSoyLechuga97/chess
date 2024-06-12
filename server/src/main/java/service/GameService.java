package service;

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
        if (gameDAO.getGame(newGameName) == -1) {
            System.out.println("A game with this name already exists :/");
            return -2;
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
        GameData oldGame =
        //Create updated game
        GameData updatedGame = new
    }
    public boolean verifyToken(AuthData userToken) throws DataAccessException {
        return authDAO.getAuth(userToken.authToken());
    }
}

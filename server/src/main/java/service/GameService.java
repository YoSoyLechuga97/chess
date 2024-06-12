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
    public boolean verifyToken(AuthData userToken) throws DataAccessException {
        return authDAO.getAuth(userToken.authToken());
    }
}

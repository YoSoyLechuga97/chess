package service;

import chess.ChessGame;
import dataaccess.*;
import exceptions.AlreadyExistsException;
import exceptions.UnauthorizedException;
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
    public int createGame(AuthData userToken, String newGameName) throws Exception {
        //Verify token
        if (!verifyToken(userToken)) { //Does not have authentication
            throw new UnauthorizedException("unauthorized");
        }
        //Check to see if game name is already in database
        ArrayList<GameData> allGames = gameDAO.listGames();
        for (GameData game : allGames) {
            if (game.gameName().equals(newGameName)) {
                throw new Exception("game with that name already exists");
            }
        }
        //Create game
        return gameDAO.createGame(userToken.authToken(), newGameName);
    }
    public boolean joinGame(AuthData userToken, String playerColor, int gameID) throws Exception {
        //Verify
        if (!verifyToken(userToken)) { //Does not have authentication
            throw new UnauthorizedException("unauthorized");
        }
        //Determine if game exists
        GameData oldGame = gameDAO.getGame(gameID);
        if (oldGame == null) {
            throw new Exception("game does not exist");
        }
        //Extract game Data
        String gameName = oldGame.gameName();
        String whitePlayer = oldGame.whiteusername();
        String blackPlayer = oldGame.blackUsername();
        ChessGame game = oldGame.game();

        //Make sure that player color isn't already taken
        if (playerColor.equals("WHITE")) {
            if (!whitePlayer.equals("NO USER")) {
                throw new AlreadyExistsException("already taken");
            } else {
                whitePlayer = userToken.username();
            }
        } else {
            if (!blackPlayer.equals("NO USER")) {
                throw new AlreadyExistsException("already taken");
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

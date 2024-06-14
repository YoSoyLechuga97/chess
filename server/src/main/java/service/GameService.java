package service;

import chess.ChessGame;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import exceptions.AlreadyExistsException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.ListGamesData;

import java.util.ArrayList;

public class GameService {
    AuthDAO authDAO = new SQLAuthDAO();
    GameDAO memoryGameDAO = new MemoryGameDAO();


    public ListGamesData listGames(AuthData userToken) throws Exception {
        //Verify user can access file
        if (!verifyToken(userToken)) { //Does not have authentication
            throw new UnauthorizedException("unauthorized");
        }
        //List all games
        return new ListGamesData(memoryGameDAO.listGames());
    }
    public int createGame(AuthData userToken, String newGameName) throws Exception {
        //Verify token
        if (!verifyToken(userToken)) { //Does not have authentication
            throw new UnauthorizedException("unauthorized");
        }
        //Check to see if game name is already in database
        ArrayList<GameData> allGames = memoryGameDAO.listGames();
        for (GameData game : allGames) {
            if (game.gameName().equals(newGameName)) {
                throw new Exception("game with that name already exists");
            }
        }
        //Create game
        return memoryGameDAO.createGame(userToken.authToken(), newGameName);
    }
    public boolean joinGame(AuthData userToken, String playerColor, int gameID) throws Exception {
        //Verify
        if (!verifyToken(userToken)) { //Does not have authentication
            throw new UnauthorizedException("unauthorized");
        }
        if (playerColor == null) {
            throw new JsonSyntaxException("playerColor cannot be null");
        }
        //Determine if game exists
        GameData oldGame = memoryGameDAO.getGame(gameID);
        if (oldGame == null) {
            throw new JsonSyntaxException("game does not exist");
        }
        //Extract game Data
        String gameName = oldGame.gameName();
        String whitePlayer = oldGame.whiteUsername();
        String blackPlayer = oldGame.blackUsername();
        ChessGame game = oldGame.game();

        //Make sure that player color isn't already taken
        String userName = authDAO.getUser(userToken.authToken());
        if (playerColor.equals("WHITE")) {
            if (whitePlayer != null) {
                throw new AlreadyExistsException("already taken");
            } else {
                whitePlayer = userName;
            }
        } else {
            if (blackPlayer != null) {
                throw new AlreadyExistsException("already taken");
            } else {
                blackPlayer = userName;
            }
        }

        //Create updated game
        GameData updatedGame = new GameData(gameID, whitePlayer, blackPlayer, gameName, game);
        memoryGameDAO.updateGame(updatedGame);
        return true;
    }
    public boolean verifyToken(AuthData userToken) throws DataAccessException {
        return authDAO.getAuth(userToken.authToken());
    }
}

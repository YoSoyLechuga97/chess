package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Random;

public class SQLGameDAO implements GameDAO{
    @Override
    public void clear() throws DataAccessException {
        databaseManager.clearTable("game");
    }

    @Override
    public int createGame(String authToken, String gameName) throws DataAccessException {
        //Make sure that game doesn't already exist

        //
        Random random = new Random();
        int newGameID = 10000000 + random.nextInt(90000000);
        databaseManager.addGame(newGameID, gameName, new ChessGame());
        return newGameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public GameData updateGame(GameData updatedGame) throws DataAccessException {
        return null;
    }
}

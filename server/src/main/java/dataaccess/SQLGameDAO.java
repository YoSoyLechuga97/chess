package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class SQLGameDAO implements GameDAO{
    @Override
    public void clear() throws DataAccessException {
        databaseManager.clearTable("game");
    }

    @Override
    public int createGame(String authToken, String gameName) throws DataAccessException {
        return 0;
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

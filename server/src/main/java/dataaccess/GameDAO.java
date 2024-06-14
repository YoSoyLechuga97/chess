package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    static DatabaseManager databaseManager = new DatabaseManager();
    static final ArrayList<GameData> GAMES = new ArrayList<>();
    public void clear() throws DataAccessException;
    public int createGame(String authToken, String gameName) throws DataAccessException;
    public GameData getGame(int gameID) throws DataAccessException;
    public ArrayList<GameData> listGames() throws DataAccessException;
    public GameData updateGame(GameData updatedGame) throws DataAccessException;
}

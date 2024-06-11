package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    static final ArrayList<GameData> games = new ArrayList<>();
    public void clear() throws DataAccessException;
    public int createGame(String authToken, String gameName) throws DataAccessException;
    public int getGame(String gameName) throws DataAccessException;
    public ArrayList<GameData> listGames() throws DataAccessException;
    public GameData updateGame(GameData updatedGame) throws DataAccessException;
}

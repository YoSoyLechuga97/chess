package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
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
        if (databaseManager.findData("game", "gameName", "gameName", gameName) != null) {
            return -1;
        }
        //
        Random random = new Random();
        int newGameID = 10000000 + random.nextInt(90000000);
        databaseManager.addGame(newGameID, gameName, new ChessGame());
        return newGameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String whiteName = databaseManager.findData("game", "gameID", "whiteUsername", String.valueOf(gameID));
        String blackName = databaseManager.findData("game", "gameID", "blackUsername", String.valueOf(gameID));
        String gameName = databaseManager.findData("game", "gameID", "gameName", String.valueOf(gameID));
        String gameString = databaseManager.findData("game", "gameID", "game", String.valueOf(gameID));
        ChessGame game = new Gson().fromJson(gameString, ChessGame.class);
        if (gameName == null) {
            return null;
        }
        return new GameData(gameID, whiteName, blackName, gameName, game);
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

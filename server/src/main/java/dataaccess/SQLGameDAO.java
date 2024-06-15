package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class SQLGameDAO implements GameDAO{
    @Override
    public void clear() throws DataAccessException {
        DATABASE_MANAGER.clearTable("game");
    }

    @Override
    public int createGame(String authToken, String gameName) throws DataAccessException {
        SQLAuthDAO token = new SQLAuthDAO();
        if (!token.getAuth(authToken)) {
            throw new DataAccessException("Authorization token not accepted");
        }
        //Make sure that game doesn't already exist
        if (DATABASE_MANAGER.findData("game", "gameName", "gameName", gameName) != null) {
            return -1;
        }
        //
        Random random = new Random();
        int newGameID = 10000000 + random.nextInt(90000000);
        DATABASE_MANAGER.addGame(newGameID, gameName, new ChessGame());
        return newGameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String whiteName = DATABASE_MANAGER.findData("game", "gameID", "whiteUsername", String.valueOf(gameID));
        String blackName = DATABASE_MANAGER.findData("game", "gameID", "blackUsername", String.valueOf(gameID));
        String gameName = DATABASE_MANAGER.findData("game", "gameID", "gameName", String.valueOf(gameID));
        String gameString = DATABASE_MANAGER.findData("game", "gameID", "game", String.valueOf(gameID));
        ChessGame game = new Gson().fromJson(gameString, ChessGame.class);
        if (gameName == null) {
            return null;
        }
        return new GameData(gameID, whiteName, blackName, gameName, game);
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return DATABASE_MANAGER.listGames();
    }

    @Override
    public GameData updateGame(GameData updatedGame) throws DataAccessException {
        //Get the old game and compare
        GameData oldGame = getGame(updatedGame.gameID());
        if (oldGame == null) {
            throw new DataAccessException("Game wasn't found");
        }
        if (!Objects.equals(updatedGame.whiteUsername(), oldGame.whiteUsername()) && oldGame.whiteUsername() == null) {
            DATABASE_MANAGER.updateData("game", "whiteUsername", updatedGame.whiteUsername(), "gameID", String.valueOf(updatedGame.gameID()));
        }
        if (!Objects.equals(updatedGame.blackUsername(), oldGame.blackUsername()) && oldGame.blackUsername() == null) {
            DATABASE_MANAGER.updateData("game", "blackUsername", updatedGame.blackUsername(), "gameID", String.valueOf(updatedGame.gameID()));
        }
        if (updatedGame.game() != oldGame.game()) {
            var json = new Gson().toJson(updatedGame.game());
            DATABASE_MANAGER.updateData("game", "game", json, "gameID", String.valueOf(updatedGame.gameID()));
        }
        return getGame(updatedGame.gameID());
    }
}

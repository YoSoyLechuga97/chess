package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class MemoryGameDAO implements GameDAO{

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public int createGame(String authToken, String gameName) throws DataAccessException {
        //Check to make sure game doesn't already exist:
        if (getGame(gameName) == -1) { //Game with this name doesn't currently exist
            //Generate random gameID
            Random random = new Random();
            int newGameID = 10000000 + random.nextInt(90000000);
            GameData newGame = new GameData(newGameID, "NO USER", "NO USER", "gameName", new ChessGame());
            return newGameID;
        }
        return -1;
    }

    @Override
    //Returns GameData if game exists, null if cannot find game
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return games;
    }

    @Override
    //Search through list of games for game to update, make updates
    public GameData updateGame(GameData updatedGame) throws DataAccessException {
        for (GameData game : games) {
            if (game.gameID() == updatedGame.gameID()) {
                games.remove(game);
                games.add(updatedGame);
            }
        }
        return updatedGame;
    }
}

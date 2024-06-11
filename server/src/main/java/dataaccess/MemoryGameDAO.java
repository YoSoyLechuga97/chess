package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO{
    ArrayList<GameData> games = new ArrayList<>();
    @Override
    public int createGame(String authToken, String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    //Returns GameID if game exists, -1 if cannot find game
    public int getGame(String gameName) throws DataAccessException {
        for (GameData game : games) {
            if (Objects.equals(game.gameName(), gameName)) {
                return game.gameID();
            }
        }
        return -1;
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

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
        for (int i = 0; i < games.size(); i++) {
            if (Objects.equals(games.get(i).gameName(), gameName)) {
                return games.get(i).gameID();
            }
        }
        return -1;
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

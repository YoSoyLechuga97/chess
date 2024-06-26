package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Random;

public class MemoryGameDAO implements GameDAO{

    @Override
    public void clear() throws DataAccessException {
        GAMES.clear();
    }

    @Override
    public int createGame(String authToken, String gameName) throws DataAccessException {
        //Check to make sure game doesn't already exist:
        ArrayList<GameData> allGames = listGames();
        for (GameData game : allGames) {
            if (game.gameName().equals(gameName)) { //Game with this name exists
                return -1;
            }
        }
        //Generate random gameID
        Random random = new Random();
        int newGameID = 10000000 + random.nextInt(90000000);
        GameData newGame = new GameData(newGameID, null, null, gameName, new ChessGame());
        GAMES.add(newGame);
        return newGameID;
    }

    @Override
    //Returns GameData if game exists, null if cannot find game
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : GAMES) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return GAMES;
    }

    @Override
    //Search through list of games for game to update, make updates
    public GameData updateGame(GameData updatedGame) throws DataAccessException {
        GAMES.removeIf(game -> game.gameID() == updatedGame.gameID());
        GAMES.add(updatedGame);
        return updatedGame;
    }
}

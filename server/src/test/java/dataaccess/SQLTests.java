package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import exceptions.AlreadyExistsException;
import model.AuthData;
import model.GameData;
import model.ListGamesData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;
import spark.utils.Assert;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SQLTests {
    @BeforeEach
    //Create my Users
    public void setup() throws Exception {
        SQLGameDAO gameDAO = new SQLGameDAO();
        if (!gameDAO.listGames().isEmpty()) {
            clearUsers();
        }
        //Create users
        UserData user1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        UserData user2 = new UserData("HiFriend", "HiFriendPassword", "HiFriend@gmail.com");
        UserData user3 = new UserData("Gamer44", "Gamer44Password", "Gamer44@aol.com");
        UserService myService = new UserService();
        GameService gameService = new GameService();
        AuthData newGuyAuth = myService.register(user1);
        AuthData hiFriendAuth = myService.register(user2);
        AuthData gamer44Auth = myService.register(user3);

        //Create some Games
        int fireGameID = gameService.createGame(newGuyAuth, "fireGame");
        int iceGameID = gameService.createGame(hiFriendAuth, "iceGame");

        //Join 1 game
        gameService.joinGame(newGuyAuth, "WHITE", fireGameID);

    }

    @Test
    @DisplayName("GetUser Success")
    public void getUserSuccess() throws Exception {
        SQLUserDAO sqlUserDAO = new SQLUserDAO();
        UserData findUser = sqlUserDAO.getUser("newGuy");
        assertNotNull(findUser, "Couldn't find existing user");
    }

    @Test
    @DisplayName("GetUser Fail")
    public void getUserFail() throws Exception {
        boolean isNull = false;
        SQLUserDAO sqlUserDAO = new SQLUserDAO();
        UserData findUser = sqlUserDAO.getUser("DNE");
        if (findUser == null) {
            isNull = true;
        }
        assertTrue(isNull, "Couldn't find existing user");
    }

    @Test
    @DisplayName("createUser Success")
    public void createUserSuccess() throws Exception {
        SQLUserDAO sqlUserDAO = new SQLUserDAO();
        UserData newUser = new UserData("harold", "haroldPassword", "harold@gmail.com");
        sqlUserDAO.createUser(newUser);

        UserData findUser = sqlUserDAO.getUser("harold");
        boolean success = findUser.username().equals("harold");
        assertTrue(success, "New User not Created");
    }

    @Test
    @DisplayName("createUser Fail")
    public void createUserFail() throws Exception {
        boolean failed = false;
        SQLUserDAO sqlUserDAO = new SQLUserDAO();
        UserData newUser = new UserData("newGuy", "haroldPassword", "harold@gmail.com");
        try {
            sqlUserDAO.createUser(newUser);
        } catch (Exception e) {
            failed = true;
        }
        assertTrue(failed, "Created duplicate usernames");
    }

    @Test
    @DisplayName("createAuth Success")
    public void createAuth() throws Exception {
        SQLAuthDAO sqlAuthDAO = new SQLAuthDAO();
        AuthData newAuth = sqlAuthDAO.createAuth("newGuy");
        assertTrue(sqlAuthDAO.getAuth(newAuth.authToken()), "Failed to create newAuth");
    }

    @Test
    @DisplayName("createAuth Fail")
    public void createAuthFail() throws Exception {
        boolean failed = false;
        SQLAuthDAO sqlAuthDAO = new SQLAuthDAO();
        try {
            AuthData newAuth = sqlAuthDAO.createAuth("bobby");
        } catch (Exception e) {
            failed = true;
        }
        assertTrue(failed, "Created newAuth for fake username");
    }

    @Test
    @DisplayName("getAuth Success")
    public void getAuth() throws Exception {
        SQLAuthDAO sqlAuthDAO = new SQLAuthDAO();
        SQLUserDAO sqlUserDAO = new SQLUserDAO();
        AuthData harold = createHarold();
        assertTrue(sqlAuthDAO.getAuth(harold.authToken()), "Failed to get authenticate");
    }

    @Test
    @DisplayName("getAuth Fail")
    public void getAuthFail() throws Exception {
        SQLAuthDAO sqlAuthDAO = new SQLAuthDAO();
        SQLUserDAO sqlUserDAO = new SQLUserDAO();
        String fakeToken = "not a real token";
        assertFalse(sqlAuthDAO.getAuth(fakeToken), "Authenticated a fake token");
    }

    @Test
    @DisplayName("getUser Success")
    public void getUserAuth() throws Exception {
        SQLAuthDAO sqlAuthDAO = new SQLAuthDAO();
        SQLUserDAO sqlUserDAO = new SQLUserDAO();
        AuthData harold = createHarold();
        String foundUsername = sqlAuthDAO.getUser(harold.authToken());
        assertEquals(foundUsername, harold.username(), "Couldn't find harold's username");
    }

    @Test
    @DisplayName("getUser Fail")
    public void getUserAuthFail() throws Exception {
        boolean failed = false;
        SQLAuthDAO sqlAuthDAO = new SQLAuthDAO();
        SQLUserDAO sqlUserDAO = new SQLUserDAO();
        String fakeToken = "fakeToken";
        String foundUsername = sqlAuthDAO.getUser(fakeToken);
        if (foundUsername == null) {
            failed = true;
        }
        assertTrue(failed, "Found a user without valid token");
    }

    @Test
    @DisplayName("deleteAuth Success")
    public void deleteAuthSuccess() throws Exception {
        SQLAuthDAO sqlAuthDAO = new SQLAuthDAO();
        AuthData harold = createHarold();
        sqlAuthDAO.deleteAuth(harold.authToken());
        assertFalse(sqlAuthDAO.getAuth(harold.authToken()), "Harold token not deleted");
    }

    @Test
    @DisplayName("deleteAuth Fail")
    public void deleteAuthFail() throws Exception {
        boolean failed = false;
        SQLAuthDAO sqlAuthDAO = new SQLAuthDAO();
        try {
            sqlAuthDAO.deleteAuth("fakeToken");
        } catch (Exception e) {
            failed = true;
        }
        assertTrue(failed, "Deleted fake token");
    }

    @Test
    @DisplayName("createGame Success")
    public void createGameSuccess() throws Exception {
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        AuthData harold = createHarold();
        int gameID = sqlGameDAO.createGame(harold.authToken(), "Harold's Game");
        assertTrue((gameID > 0),"Failed to create game");
    }

    @Test
    @DisplayName("createGame Fail")
    public void createGameFail() throws Exception {
        boolean failed = false;
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        AuthData harold = createHarold();
        //Create existing game
        int gameID = sqlGameDAO.createGame(harold.authToken(), "fireGame");
        assertTrue((gameID == -1),"Created game with same name as existing game");
        //Not authorized to make game
        try {
            sqlGameDAO.createGame("fakeToken", "fakeGame");
        } catch (Exception e) {
            failed = true;
        }
        assertTrue(failed, "Created game without authentication");
    }

    @Test
    @DisplayName("getGame Success")
    public void getGameSuccess() throws Exception {
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        AuthData harold = createHarold();
        int gameID = sqlGameDAO.createGame(harold.authToken(), "Harold's Game");
        GameData foundGame = sqlGameDAO.getGame(gameID);
        assertEquals("Harold's Game", foundGame.gameName(), "Failed to find game");
    }

    @Test
    @DisplayName("getGame Fail")
    public void getGameFail() throws Exception {
        boolean failed = false;
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        AuthData harold = createHarold();
        int fakeID = 21;
        GameData foundGame = sqlGameDAO.getGame(fakeID);
        if (foundGame == null) {
            failed = true;
        }
        assertTrue(failed, "Found false game");
    }

    @Test
    @DisplayName("listGame Success")
    public void listGameSuccess() throws Exception {
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        assertNotNull(sqlGameDAO.listGames(), "Did not find any games");
    }

    @Test
    @DisplayName("listGame Fail")
    public void listGameFail() throws Exception {
        boolean failed = false;
        ArrayList<GameData> allGames = new ArrayList<>();
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        clearUsers();
        try {
            allGames = sqlGameDAO.listGames();
        } catch (Exception e) {
            failed = true;
        }
        if (allGames.isEmpty()) {
            failed = true;
        }
        assertTrue(failed, "Listed nonexistant games");
    }

    @Test
    @DisplayName("updateGame Success")
    public void updateGame() throws Exception {
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        AuthData harold = createHarold();
        int joinID = sqlGameDAO.createGame(harold.authToken(), "Harold's Game");
        //Harold join white
        GameData haroldJoins = new GameData(joinID, harold.username(), null, "Harold's Game", new ChessGame());
        GameData testJoin = sqlGameDAO.updateGame(haroldJoins);
        assertEquals(testJoin.whiteUsername(), harold.username(), "Harold didn't join");
        //Harold makes move
        testJoin.game().makeMove(new ChessMove(new ChessPosition(2,3), new ChessPosition(3, 3), null));
        GameData testMove = sqlGameDAO.updateGame(testJoin);
        ChessGame moveGame = new ChessGame();
        moveGame.makeMove(new ChessMove(new ChessPosition(2,3), new ChessPosition(3, 3), null));
        assertEquals(moveGame, testMove.game(), "Move was not registered");
    }

    @Test
    @DisplayName("updateGame Fail")
    public void updateGameFail() throws Exception {
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        AuthData harold = createHarold();
        int joinID = sqlGameDAO.createGame(harold.authToken(), "Harold's Game");
        //Harold join white
        GameData haroldJoins = new GameData(joinID, harold.username(), null, "Harold's Game", new ChessGame());
        GameData testJoin = sqlGameDAO.updateGame(haroldJoins);
        assertEquals(testJoin.whiteUsername(), harold.username(), "Harold didn't join");
        //newGuy tries to join white
        GameData newGuyJoins = new GameData(joinID, "newGuy", null, "Harold's Game", new ChessGame());
        GameData testReplace = sqlGameDAO.updateGame(newGuyJoins);
        assertNotEquals("newGuy", testReplace.whiteUsername(), "newGuy replaced Harold");
    }

    public AuthData createHarold() throws AlreadyExistsException, DataAccessException {
        UserData newUser = new UserData("harold", "haroldPassword", "harold@gmail.com");
        UserService myService = new UserService();
        AuthData harold = myService.register(newUser);
        return harold;
    }

    @Test
    @DisplayName("Clear Users")
    public void clearUsers() throws Exception {
        //Login to database
        UserService myService = new UserService();
        boolean thrown = false;
        //Good Login
        UserData login1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual1 = myService.login(login1);
        Assertions.assertNotEquals(null, actual1, "Failed to successfully login");
        //Clear Database
        myService.clear();
        //Attempt to login with empty database
        try {
            AuthData actual2 = myService.login(login1);
        } catch (Exception e){
            thrown = true;
        }
        assertTrue(thrown, "Still logged in, database not cleared");
    }
}

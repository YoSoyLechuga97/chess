package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.UserService;
import org.eclipse.jetty.server.Authentication;
import spark.utils.Assert;

import javax.xml.crypto.Data;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class myTests {
    @BeforeEach
    //Create my Users
    public void setup() throws DataAccessException{
        //Create users
        UserData user1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        UserData user2 = new UserData("HiFriend", "HiFriendPassword", "HiFriend@gmail.com");
        UserData user3 = new UserData("Gamer44", "Gamer44Password", "Gamer44@aol.com");
        UserService myService = new UserService();
        myService.register(user1);
        myService.register(user2);
        myService.register(user3);
    }

    @Test
    @DisplayName("Clear Users")
    public void clearUsers() throws DataAccessException {
        UserService myService = new UserService();
        myService.clear();
    }

    @Test
    @DisplayName("Login Attempt")
    public void logginIn() throws DataAccessException {
        UserService myService = new UserService();
        //Good Login
        UserData login1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual1 = myService.login(login1);
        Assertions.assertNotEquals(null, actual1, "Failed to successfully login");
        //Bad Login
        UserData login2 = new UserData("newGuy", "newGuyPasswordWrong", "newGuyEmail@yahoo.com");
        AuthData actual2 = myService.login(login2);
        assertNull(actual2, "Failed to stop login with bad password");
    }

    @Test
    @DisplayName("Register Tests")
    public void register() throws DataAccessException {
        UserService myService = new UserService();
        //Successful Register
        UserData register1 = new UserData("Chuga97", "Chuga97Password", "Chuga97@gmail.com");
        AuthData actual1 = myService.register(register1);
        assertNotNull(actual1, "Unable to register new user");

        //Existing User
        UserData register2 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual2 = myService.register(register2);
        assertNull(actual2, "Registered existing user");
    }

    @Test
    @DisplayName("Logout Test")
    public void logout() throws DataAccessException {
        UserService myService = new UserService();
        //Successful logout
        UserData user1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData newPC = myService.login(user1);
        boolean actual = myService.logout(newPC);
        Assert.isTrue(actual, "Was unable to logout existing user");

        //Unsuccessful logout
        AuthData fakePC = new AuthData("notARealToken", "newGuy");
        boolean actual2 = myService.logout(fakePC);
        Assert.isTrue(!actual2, "Successfully Logged out fake token");
    }

    @Test
    @DisplayName("Create a Game")
    public void createGame() throws DataAccessException {
        UserService myService = new UserService();
        GameService gameService = new GameService();
        UserData login1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual1 = myService.login(login1);

        //Successfully create a game
        int gameID = gameService.createGame(actual1, "NewGame!");
        boolean createdNewGame = (gameID > 0);
        Assert.isTrue(createdNewGame, "New Game failed to be created");

        //Unsuccessfully create game
        int badGameID = gameService.createGame(actual1, "NewGame!");
        boolean noNewGame = (badGameID < 0);
        Assert.isTrue(noNewGame, "Game that should not have been made was created");
    }

    @Test
    @DisplayName("ListGames")
    public void listGames() throws DataAccessException{
        UserService myService = new UserService();
        GameService gameService = new GameService();
        UserData login1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual1 = myService.login(login1);

        //Create multiple games
        gameService.createGame(actual1, "NewGame!");
        gameService.createGame(actual1, "BattleTime");
        gameService.createGame(actual1, "Cole's Game");

        //Successfully List Games
        ArrayList<GameData> allGames = gameService.listGames(actual1);
        Assert.notNull(allGames, "No Games listed, expected three");

        //Fail to List Games
        AuthData falseData = new AuthData("a", "newGuy");
        ArrayList<GameData> noGames = gameService.listGames(falseData);
        assertNull(noGames, "Still listed games without authentication");
    }

    @Test
    @DisplayName("Join Game")
    public void joinGame() throws DataAccessException{
        UserService myService = new UserService();
        GameService gameService = new GameService();
        UserData login1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual1 = myService.login(login1);

        //Successfully Join a Game
        int joinID = gameService.createGame(actual1, "Cole's Game");
        boolean joined = gameService.joinGame(actual1, "WHITE", joinID);
        Assert.isTrue(joined, "Failed to successfully join game");

        //Fail to join game
        boolean noJoin = !gameService.joinGame(actual1, "WHITE", joinID);
        Assert.isTrue(noJoin, "Joined a game that you should not have");
    }
}

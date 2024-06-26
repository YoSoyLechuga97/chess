package service;

import model.AuthData;
import model.ListGamesData;
import model.UserData;
import org.junit.jupiter.api.*;
import spark.utils.Assert;

import static org.junit.jupiter.api.Assertions.*;

public class MyTests {
    @BeforeEach
    //Create my Users
    public void setup() throws Exception {
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
    @DisplayName("Login Success")
    public void logIn() throws Exception {
        UserService myService = new UserService();
        //Good Login
        UserData login1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual1 = myService.login(login1);
        Assertions.assertNotEquals(null, actual1, "Failed to successfully login");
        clearUsers();
    }

    @Test
    @DisplayName("Login Fail")
    public void logInFail() throws Exception {
        UserService myService = new UserService();
        boolean thrown = false;
        //Bad Login
        UserData login2 = new UserData("newGuy", "newGuyPasswordWrong", "newGuyEmail@yahoo.com");
        try {
            AuthData actual2 = myService.login(login2);
        } catch (Exception e){
            thrown = true;
        }
        Assertions.assertTrue(thrown, "Failed to stop login with bad password");
        clearUsers();
    }

    @Test
    @DisplayName("Register Tests")
    public void register() throws Exception {
        UserService myService = new UserService();
        //Successful Register
        UserData register1 = new UserData("Chuga97", "Chuga97Password", "Chuga97@gmail.com");
        AuthData actual1 = myService.register(register1);
        assertNotNull(actual1, "Unable to register new user");
        clearUsers();
    }

    @Test
    @DisplayName("Register Tests Fail")
    public void registerFail() throws Exception {
        UserService myService = new UserService();
        boolean thrown = false;
        //Existing User
        UserData register2 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        try {
            AuthData actual2 = myService.register(register2);
        } catch (Exception e){
            thrown = true;
        }
        assertTrue(thrown, "Registered existing user");
        clearUsers();
    }

    @Test
    @DisplayName("Logout Success")
    public void logout() throws Exception {
        UserService myService = new UserService();
        //Successful logout
        UserData user1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData newPC = myService.login(user1);
        boolean actual = myService.logout(newPC);
        Assert.isTrue(actual, "Was unable to logout existing user");
        clearUsers();
    }

    @Test
    @DisplayName("Logout Fail")
    public void logoutFail() throws Exception {
        UserService myService = new UserService();
        boolean thrown = false;
        //Unsuccessful logout
        AuthData fakePC = new AuthData("notARealToken", "newGuy");
        try {
            boolean actual2 = myService.logout(fakePC);
        }catch (Exception e){
            thrown = true;
        }
        Assert.isTrue(thrown, "Successfully Logged out fake token");
        clearUsers();
    }

    @Test
    @DisplayName("Create a Game Success")
    public void createGame() throws Exception {
        UserService myService = new UserService();
        GameService gameService = new GameService();
        UserData login1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual1 = myService.login(login1);

        //Successfully create a game
        int gameID = gameService.createGame(actual1, "NewGame!");
        boolean createdNewGame = (gameID > 0);
        Assert.isTrue(createdNewGame, "New Game failed to be created");
        clearUsers();
    }

    @Test
    @DisplayName("Create a Game Fail")
    public void createGameFail() throws Exception {
        UserService myService = new UserService();
        GameService gameService = new GameService();
        boolean thrown = false;
        UserData login1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual1 = myService.login(login1);

        //Successfully create a game
        int gameID = gameService.createGame(actual1, "NewGame!");

        //Unsuccessfully create game
        try {
            int badGameID = gameService.createGame(actual1, "NewGame!");
        } catch (Exception e){
            thrown = true;
        }
        Assert.isTrue(thrown, "Game that should not have been made was created");
        clearUsers();
    }

    @Test
    @DisplayName("ListGames Success")
    public void listGames() throws Exception {
        UserService myService = new UserService();
        GameService gameService = new GameService();
        UserData login1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual1 = myService.login(login1);

        //Create multiple games
        gameService.createGame(actual1, "NewGame!");
        gameService.createGame(actual1, "BattleTime");
        gameService.createGame(actual1, "Cole's Game");

        //Successfully List Games
        ListGamesData allGames = gameService.listGames(actual1);
        Assert.notNull(allGames, "No Games listed, expected three");
        clearUsers();
    }

    @Test
    @DisplayName("ListGames Fail")
    public void listGamesFail() throws Exception {
        UserService myService = new UserService();
        GameService gameService = new GameService();
        boolean thrown = false;
        UserData login1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual1 = myService.login(login1);

        //Create multiple games
        gameService.createGame(actual1, "NewGame!");
        gameService.createGame(actual1, "BattleTime");
        gameService.createGame(actual1, "Cole's Game");

        //Fail to List Games
        AuthData falseData = new AuthData("a", "newGuy");
        try {
            ListGamesData noGames = gameService.listGames(falseData);
        } catch (Exception e){
            thrown = true;
        }
        assertTrue(thrown, "Still listed games without authentication");
        clearUsers();
    }

    @Test
    @DisplayName("Join Game")
    public void joinGame() throws Exception {
        UserService myService = new UserService();
        GameService gameService = new GameService();
        UserData login1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual1 = myService.login(login1);

        //Successfully Join a Game
        int joinID = gameService.createGame(actual1, "Cole's Game");
        boolean joined = gameService.joinGame(actual1, "WHITE", joinID);
        Assert.isTrue(joined, "Failed to successfully join game");
        clearUsers();
    }

    @Test
    @DisplayName("Join Game Fail")
    public void joinGameFail() throws Exception {
        UserService myService = new UserService();
        GameService gameService = new GameService();
        boolean thrown = false;
        UserData login1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual1 = myService.login(login1);

        //Successfully Join a Game
        int joinID = gameService.createGame(actual1, "Cole's Game");
        boolean joined = gameService.joinGame(actual1, "WHITE", joinID);

        //Join game with color already in use
        try {
            boolean noJoin = !gameService.joinGame(actual1, "WHITE", joinID);
        } catch (Exception e){
            thrown = true;
        }
        Assert.isTrue(thrown, "Joined a game that you should not have");

        thrown = false;
        //Join game that does not exist
        int fakeGame = 12;
        try {
            boolean fakeJoin = !gameService.joinGame(actual1, "WHITE", fakeGame);
        } catch (Exception e){
            thrown = true;
        }
        Assert.isTrue(thrown, "Joined a game that does not exist");
        clearUsers();
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

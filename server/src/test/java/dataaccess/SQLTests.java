package dataaccess;

import exceptions.AlreadyExistsException;
import model.AuthData;
import model.ListGamesData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;
import spark.utils.Assert;

import static org.junit.jupiter.api.Assertions.*;

public class SQLTests {
    @BeforeEach
    //Create my Users
    public void setup() throws Exception {
        clearUsers();
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

    public AuthData createHarold() throws AlreadyExistsException, DataAccessException {
        UserData newUser = new UserData("harold", "haroldPassword", "harold@gmail.com");
        UserService myService = new UserService();
        AuthData harold = myService.register(newUser);
        return harold;
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

package client;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import exceptions.AlreadyExistsException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import facade.ServerFacade;
import service.GameService;
import service.UserService;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void startup() throws Exception {
        SQLGameDAO gameDAO = new SQLGameDAO();
        if (!gameDAO.listGames().isEmpty()) {
            clearUsers();
        }
        //Create users
        UserData newGuy = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        UserData hiFriend = new UserData("HiFriend", "JeepersCreepers", "zoinks@gmail.com");
        UserData bobaFett = new UserData("BobaFett", "BobaPassword", "Fett@aol.com");
        UserService theService = new UserService();
        GameService serviceGame = new GameService();
        AuthData regisNewGuy = theService.register(newGuy);
        AuthData jinkies = theService.register(hiFriend);
        theService.register(bobaFett);

        //Create some Games
        int fireGameID = serviceGame.createGame(regisNewGuy, "fireGame");
        serviceGame.createGame(jinkies, "iceGame");

        //Join 1 game
        serviceGame.joinGame(regisNewGuy, "WHITE", fireGameID);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @DisplayName("Login Success")
    public void loginSuccess() throws URISyntaxException, IOException {
        AuthData authData = facade.login("newGuy", "newGuyPassword");
        assertNotNull(authData, "Unable to login user");
    }

    @Test
    @DisplayName("Login Fail")
    public void loginFail() throws URISyntaxException, IOException {
        boolean failed = false;
        AuthData authData = facade.login("fakename", "dslkjsfdklj");
        if(authData == null) {
            failed = true;
        }
        assertTrue(failed, "Logged in fake user");
    }

    @Test
    @DisplayName("Register Success")
    public void registerSuccess() throws URISyntaxException, IOException {
        AuthData authData = facade.register("harold", "haroldPassword", "imharold@harold.com");
        assertNotNull(authData, "failed to register harold :(");
    }

    @Test
    @DisplayName("Register Fail")
    public void registerFail() throws URISyntaxException, IOException {
        boolean failed = false;
        AuthData authData = facade.register("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        if (authData == null) {
            failed = true;
        }
        assertTrue(failed, "registered user with existing username");
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess() throws URISyntaxException, IOException, DataAccessException {
        boolean loggedOut = false;
        AuthData authData = facade.login("newGuy", "newGuyPassword");
        AuthData logoutData = facade.logout(authData);
        SQLAuthDAO sqlAuthDAO = new SQLAuthDAO();
        String name = sqlAuthDAO.getUser(authData.authToken());
        if (name == null) {
            loggedOut = true;
        }
        assertTrue(loggedOut, "Failed to logout user");
    }

    @Test
    @DisplayName("Logout Failure")
    public void logoutFail() throws URISyntaxException, IOException, DataAccessException {
        AuthData authData = facade.register("harold", "haroldPassword", "imharold@harold.com");
        AuthData fakeID = new AuthData("FakeToken", "harold");
        AuthData logout = facade.logout(fakeID);
        SQLAuthDAO sqlAuthDAO = new SQLAuthDAO();
        String name = sqlAuthDAO.getUser(authData.authToken());
        assertEquals(name, authData.username(), "Harold was logged out :(");
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGame() throws URISyntaxException, IOException, DataAccessException {
        boolean gameMade = false;
        AuthData harold = createHarold();
        facade.createGame(harold, "moneyBall");
        SQLGameDAO gameDAO = new SQLGameDAO();
        ArrayList<GameData> allGames = gameDAO.listGames();
        for (GameData game : allGames) {
            if (game.gameName().equals("moneyBall")) {
                gameMade = true;
            }
        }
        assertTrue(gameMade, "Harold's game was not made :(");
    }

    @Test
    @DisplayName("Create Game Fail")
    public void createFail() throws URISyntaxException, IOException, DataAccessException {
        boolean gameMade = false;
        AuthData fake = new AuthData("FakeToken", "FakeUser");
        facade.createGame(fake, "moneyBall");
        SQLGameDAO gameDAO = new SQLGameDAO();
        ArrayList<GameData> allGames = gameDAO.listGames();
        for (GameData game : allGames) {
            if (game.gameName().equals("moneyBall")) {
                gameMade = true;
            }
        }
        assertFalse(gameMade, "Fake game was made");
    }

    @Test
    @DisplayName("List Games Success")
    public void listGamesSuccess() throws URISyntaxException, IOException {
        AuthData harold = createHarold();
        ArrayList<GameData> allGames = facade.listGames(harold);
        assertFalse(allGames.isEmpty(), "No Games Listed");
    }

    @Test
    @DisplayName("List Games Fail")
    public void listFail() throws URISyntaxException, IOException {
        boolean gamesListed = true;
        AuthData fake = new AuthData("FakeToken", "Fakeuser");
        ArrayList<GameData> allGames = facade.listGames(fake);
        if (allGames == null) {
            gamesListed = false;
        }
        assertFalse(gamesListed, "Games were listed with fake token");
    }

    @Test
    @DisplayName("Join Game Success")
    public void joinGame() throws URISyntaxException, IOException {
        boolean joined = false;
        AuthData harold = createHarold();
        ArrayList<GameData> allGames = facade.listGames(harold);
        int gameID = 0;
        for (GameData game : allGames) {
            if (game.gameName().equals("fireGame")) {
                gameID = game.gameID();
            }
        }
        facade.joinGame(harold, "BLACK", gameID);
        allGames = facade.listGames(harold);
        for (GameData game : allGames) {
            if (game.blackUsername() != null) {
                if (game.blackUsername().equals(harold.username())) {
                    joined = true;
                }
            }
        }
        assertTrue(joined, "Harold couldn't join the game :(");
    }

    @Test
    @DisplayName("Join Game Fail")
    public void joinFail() throws URISyntaxException, IOException {
        boolean joined = false;
        AuthData harold = createHarold();
        AuthData fakeUser = new AuthData("FakeToken", "FakeUserName");
        ArrayList<GameData> allGames = facade.listGames(harold);
        int gameID = 0;
        for (GameData game : allGames) {
            if (game.gameName().equals("fireGame")) {
                gameID = game.gameID();
            }
        }
        facade.joinGame(fakeUser, "BLACK", gameID);
        allGames = facade.listGames(harold);
        for (GameData game : allGames) {
            if (game.blackUsername() != null) {
                if (game.blackUsername().equals(fakeUser.username())) {
                    joined = true;
                }
            }
        }
        assertFalse(joined, "Fake user stole harold's spot!");
    }

    public AuthData createHarold () throws URISyntaxException, IOException {
        return facade.register("harold", "haroldPassword", "imharold@harold.com");
    }

    public static void clearUsers() throws Exception {
        //Login to database
        UserService service = new UserService();
        boolean isTossed = false;
        //Good Login
        UserData login1 = new UserData("HiFriend", "HiFriendPassword", "HiFriend@gmail.com");
        AuthData actual1 = service.login(login1);
        assertNotEquals(null, actual1, "Login was not successful");
        //Clear Database
        service.clear();
        //Login with empty db
        try {
            AuthData actual2 = service.login(login1);
        } catch (Exception e){
            isTossed = true;
        }
        assertTrue(isTossed, "Logged in after db was supposedly cleared");
    }

}

package client;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import facade.ServerFacade;
import service.GameService;
import service.UserService;

import java.io.IOException;
import java.net.URISyntaxException;

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

    public static void clearUsers() throws Exception {
        //Login to database
        UserService service = new UserService();
        boolean isTossed = false;
        //Good Login
        UserData login1 = new UserData("HiFriend", "HiFriendPassword", "HiFriend@gmail.com");
        AuthData actual1 = service.login(login1);
        Assertions.assertNotEquals(null, actual1, "Login was not successful");
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

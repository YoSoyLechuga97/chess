package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.UserService;
import org.eclipse.jetty.server.Authentication;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class myTests {
    @BeforeEach
    //Create my Users
    public void setup() throws DataAccessException{
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
        System.out.println("Using correct login information");
        UserData login1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
        AuthData actual1 = myService.login(login1);
        Assertions.assertNotEquals(null, actual1, "Failed to successfully login");

        System.out.println("Using Incorrect information");
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
}

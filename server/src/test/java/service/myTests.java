package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import service.UserService;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        if (actual1 == null) {
            System.out.println("FAILED TO LOGIN");
        }

        System.out.println("Using Incorrect information");
        UserData login2 = new UserData("newGuy", "newGuyPasswordWrong", "newGuyEmail@yahoo.com");
        AuthData actual2 = myService.login(login2);
        if (actual2 != null) {
            System.out.println("FAILED TO STOP LOGIN");
        }
    }
}

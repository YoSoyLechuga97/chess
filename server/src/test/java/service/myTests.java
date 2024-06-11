package service;

import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class myTests {
    @BeforeAll
    //Create my Users
    UserData user1 = new UserData("newGuy", "newGuyPassword", "newGuyEmail@yahoo.com");
    UserData user2 = new UserData("HiFriend", "HiFriendPassword", "HiFriend@gmail.com");
    UserData user3 = new UserData("Gamer44", "Gamer44Password", "Gamer44@aol.com");

    @Test
    @DisplayName("Clear Users")
    public void clearUsers() {

    }
}

package service;

import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import exceptions.AlreadyExistsException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class UserService {
    UserDAO memoryUserDAO = new MemoryUserDAO();

    AuthDAO authDAO = new SQLAuthDAO();
    AuthDAO memoryAuthDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    public AuthData register(UserData user) throws DataAccessException, AlreadyExistsException {
        //Check that all information is included
        if (user.password() == null || user.username() == null || user.email() == null) {
            throw new JsonSyntaxException("bad request");
        }
        if (memoryUserDAO.getUser(user.username()) == null) {
            memoryUserDAO.createUser(user);
            return authDAO.createAuth(user.username());
        } else {
            throw new AlreadyExistsException("already taken");
        }
    }
    public AuthData login(UserData user) throws DataAccessException, UnauthorizedException {
        //Check that all information is included
        if (user.password() == null || user.username() == null) {
            throw new JsonSyntaxException("bad request");
        }
        if (memoryUserDAO.getUser(user.username()) != null) {
            if (Objects.equals(memoryUserDAO.getUser(user.username()).password(), user.password())) {
                return authDAO.createAuth(user.username());
            }
        }
        throw new UnauthorizedException("unauthorized");
    }
    public boolean logout(AuthData user) throws DataAccessException, UnauthorizedException {
        //Verify that request is good
        if (user.authToken() == null) {
            throw new JsonSyntaxException("bad request");
        }
        if (authDAO.getAuth(user.authToken())) {
            authDAO.deleteAuth(user.authToken());
            return true;
        } else {
            throw new UnauthorizedException("unauthorized");
        }
    }
    public void clear() throws DataAccessException {
        memoryUserDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}

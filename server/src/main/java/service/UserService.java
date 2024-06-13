package service;

import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import exceptions.AlreadyExistsException;
import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class UserService {
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    public AuthData register(UserData user) throws DataAccessException, AlreadyExistsException {
        //Check that all information is included
        if (user.password() == null || user.username() == null || user.email() == null) {
            throw new JsonSyntaxException("bad request");
        }
        if (userDAO.getUser(user.username()) == null) {
            userDAO.createUser(user);
            return authDAO.createAuth(user.username());
        } else {
            throw new AlreadyExistsException("already taken");
        }
    }
    public AuthData login(UserData user) throws DataAccessException {
        if (userDAO.getUser(user.username()) != null) {
            if (Objects.equals(userDAO.getUser(user.username()).password(), user.password())) {
                return authDAO.createAuth(user.username());
            }
        }
        return null;
    }
    public boolean logout(AuthData user) throws DataAccessException{
        if (authDAO.getAuth(user.authToken())) {
            authDAO.deleteAuth(user.authToken());
            return true;
        } else {
            return false;
        }
    }
    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}

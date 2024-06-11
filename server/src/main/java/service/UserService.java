package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class UserService {
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    public AuthData register(UserData user) throws DataAccessException {
        if (userDAO.getUser(user.username()) == null) {
            userDAO.createUser(user);
            return authDAO.createAuth(user.username());
        }
        return null;
    }
    public AuthData login(UserData user) throws DataAccessException {
        if (userDAO.getUser(user.username()) != null) {
            if (Objects.equals(userDAO.getUser(user.username()).password(), user.password())) {
                return authDAO.createAuth(user.username());
            }
        }
        return null;
    }
    public void logout(UserData user) {};
    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}

package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.ArrayList;

public class UserService {
    UserDAO userDAO;
    AuthDAO authDAO;
    public AuthData register(UserData user) throws DataAccessException {
        if (userDAO.getUser(user.username()) == null) {
            userDAO.createUser(user);
            return authDAO.createAuth(user.username());
        }
        return null;
    }
    public AuthData login(UserData user) throws DataAccessException {
        if (userDAO.getUser(user.username()) != null) {
            if (userDAO.getUser(user.username()).password() == user.password()) {
                return authDAO.createAuth(user.username());
            }
        }
        return null;
    }
    public void logout(UserData user) {};
}

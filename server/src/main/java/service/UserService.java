package service;

import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import exceptions.AlreadyExistsException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class UserService {
    UserDAO userDAO = new SQLUserDAO();
    AuthDAO authDAO = new SQLAuthDAO();
    GameDAO gameDAO = new SQLGameDAO();
    public AuthData register(UserData user) throws DataAccessException, AlreadyExistsException {
        //Check that all information is included
        if (user.password() == null || user.username() == null || user.email() == null) {
            throw new JsonSyntaxException("bad request");
        }
        if (userDAO.getUser(user.username()) == null) {
            //Hash user Password
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            UserData hashedUser = new UserData(user.username(), hashedPassword, user.email());
            userDAO.createUser(hashedUser);
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
        if (userDAO.getUser(user.username()) != null) {
            if (Objects.equals(userDAO.getUser(user.username()).password(), user.password())) {
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
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.createDatabase();
    }
}

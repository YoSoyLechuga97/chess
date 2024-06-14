package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

public class SQLUserDAO implements UserDAO{
    @Override
    public void clear() throws DataAccessException {
        databaseManager.clearTable("user");
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        databaseManager.addUser(userData.username(), userData.password(), userData.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String userPassword = databaseManager.findData("user", "username", "password", username);
        String userEmail = databaseManager.findData("user", "username", "email", username);
        if (userPassword == null && userEmail == null) {
            return null;
        }
        return new UserData(username, userPassword, userEmail);
    }
}

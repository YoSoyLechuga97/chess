package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO{
    @Override
    public void clear() throws DataAccessException {
        DATABASE_MANAGER.clearTable("user");
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        DATABASE_MANAGER.addUser(userData.username(), userData.password(), userData.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String userPassword = DATABASE_MANAGER.findData("user", "username", "password", username);
        String userEmail = DATABASE_MANAGER.findData("user", "username", "email", username);
        if (userPassword == null && userEmail == null) {
            return null;
        }
        return new UserData(username, userPassword, userEmail);
    }
}

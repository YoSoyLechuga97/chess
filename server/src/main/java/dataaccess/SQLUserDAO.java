package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO{
    @Override
    public void clear() throws DataAccessException {
        databaseManager.clearTable("user");
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }
}

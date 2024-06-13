package dataaccess;

import model.UserData;

public class MemoryUserDAO implements UserDAO{

    @Override
    public void clear() throws DataAccessException {
        USERS.clear();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        USERS.add(userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : USERS) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }
}

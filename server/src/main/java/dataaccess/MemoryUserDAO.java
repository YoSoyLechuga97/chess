package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO{

    static final ArrayList<UserData> users = new ArrayList<>();

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        users.add(userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        int userIndex = -1;
        //Search through the array for the matching username
        for (int i = 0; i < users.size(); i++) {
            if (Objects.equals(users.get(i).username(), username)) {
                userIndex = i;
            }
        }
        //Return userData if there, if not, return null
        if (userIndex >= 0) {
            return users.get(userIndex);
        } else {
            return null;
        }
    }
}

package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO{

    static final ArrayList<UserData> users = new ArrayList<>();

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public void createUser(UserData userData) {
        users.add(userData);
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }
}

package dataaccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDAO {
    static DatabaseManager DATABASE_MANAGER = new DatabaseManager();
    static final ArrayList<UserData> USERS = new ArrayList<>();
    public void clear() throws DataAccessException;
    public void createUser(UserData userData) throws DataAccessException;
    public UserData getUser(String username) throws DataAccessException;
    }

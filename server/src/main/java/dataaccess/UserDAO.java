package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.ArrayList;

public interface UserDAO {
    static final ArrayList<UserData> users = new ArrayList<>();
    public void clear() throws DataAccessException;
    public void createUser(UserData userData) throws DataAccessException;
    public UserData getUser(String username) throws DataAccessException;
    }

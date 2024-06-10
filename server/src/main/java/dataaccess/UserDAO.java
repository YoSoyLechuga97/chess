package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

public interface UserDAO {
    public void clear() throws DataAccessException;
    public void createUser(UserData userData) throws DataAccessException;
    public UserData getUser(String username) throws DataAccessException;
    }

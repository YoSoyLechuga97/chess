package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

public interface UserDAO {
    public void clear();
    public void createUser(UserData userData);
    public UserData getUser(String username);
    }

package dataaccess;

import model.AuthData;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface AuthDAO {
    ArrayList<AuthData> auths = new ArrayList<>();
    public void clear() throws DataAccessException;
    public AuthData createAuth(String username) throws DataAccessException;
    public boolean getAuth(String token) throws DataAccessException;
    public void deleteAuth(String token) throws DataAccessException;

}

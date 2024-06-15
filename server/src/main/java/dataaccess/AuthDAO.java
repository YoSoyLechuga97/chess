package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public interface AuthDAO {
    static DatabaseManager DATABASE_MANAGER = new DatabaseManager();
    static final ArrayList<AuthData> AUTHS = new ArrayList<>();
    public void clear() throws DataAccessException;
    public AuthData createAuth(String username) throws DataAccessException;
    public boolean getAuth(String token) throws DataAccessException;
    public String getUser(String token) throws DataAccessException;
    public void deleteAuth(String token) throws DataAccessException;

}

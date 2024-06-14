package dataaccess;

import model.AuthData;

import java.sql.DriverManager;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO{
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        //Generate a random authentication token
        String newToken = UUID.randomUUID().toString();

        //Add to Database
        databaseManager.addAuth(newToken, username);

        //Save as new data model
        return new AuthData(newToken, username);
    }

    @Override
    public boolean getAuth(String token) throws DataAccessException {
        return false;
    }

    @Override
    public String getUser(String token) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {

    }
}

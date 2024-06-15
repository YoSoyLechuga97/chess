package dataaccess;

import model.AuthData;

import java.sql.DriverManager;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO{
    @Override
    public void clear() throws DataAccessException {
        databaseManager.clearTable("auth");
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        SQLUserDAO user = new SQLUserDAO();
        if (user.getUser(username) == null) {
            throw new DataAccessException("User does not exist");
        }
        //Generate a random authentication token
        String newToken = UUID.randomUUID().toString();

        //Add to Database
        databaseManager.addAuth(newToken, username);

        //Save as new data model
        return new AuthData(newToken, username);
    }

    @Override
    public boolean getAuth(String token) throws DataAccessException {
        if (databaseManager.findData("auth", "authToken", "username", token) != null) {
            return true;
        }
        return false;
    }

    @Override
    public String getUser(String token) throws DataAccessException {
        return databaseManager.findData("auth", "authToken", "username", token);
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        if (getAuth(token)) {
            databaseManager.deleteData("auth", "authToken", token);
        } else {
            throw new DataAccessException("Token does not exist");
        }
    }
}

package dataaccess;

import model.AuthData;

import java.util.UUID;

public class SQLAuthDAO implements AuthDAO{
    @Override
    public void clear() throws DataAccessException {
        DATABASE_MANAGER.clearTable("auth");
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
        DATABASE_MANAGER.addAuth(newToken, username);

        //Save as new data model
        return new AuthData(newToken, username);
    }

    @Override
    public boolean getAuth(String token) throws DataAccessException {
        if (DATABASE_MANAGER.findData("auth", "authToken", "username", token) != null) {
            return true;
        }
        return false;
    }

    @Override
    public String getUser(String token) throws DataAccessException {
        return DATABASE_MANAGER.findData("auth", "authToken", "username", token);
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        if (getAuth(token)) {
            DATABASE_MANAGER.deleteData("auth", "authToken", token);
        } else {
            throw new DataAccessException("Token does not exist");
        }
    }
}

package dataaccess;

import model.AuthData;

import java.security.SecureRandom;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        //Generate a random authentication token
        String newToken = UUID.randomUUID().toString();

        //Create new data model
        AuthData newAuth = new AuthData(newToken, username);

        //Add to list
        auths.add(newAuth);
        return newAuth;
    }

    @Override
    public boolean getAuth(String token) throws DataAccessException {
        for (AuthData currToken : auths) {
            if (currToken.authToken().equals(token)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getUser(String token) throws DataAccessException {
        for (AuthData currToken : auths) {
            if (currToken.authToken().equals(token)) {
                return currToken.username();
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        auths.removeIf(currToken -> currToken.authToken().equals(token));
    }
}

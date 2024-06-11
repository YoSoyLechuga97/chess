package dataaccess;

import model.AuthData;

import java.security.SecureRandom;

public class MemoryAuthDAO implements AuthDAO{
    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        //Generate a random authentication token
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[8 / 2];
        secureRandom.nextBytes(tokenBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : tokenBytes) {
            sb.append(String.format("%02x", b));
        }
        String newToken = sb.toString();

        //Create new data model
        AuthData newAuth = new AuthData(newToken, username);

        //Add to list
        auths.add(newAuth);
        return newAuth;
    }

    @Override
    public boolean getAuth(String token) throws DataAccessException {
        return false;
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {

    }
}

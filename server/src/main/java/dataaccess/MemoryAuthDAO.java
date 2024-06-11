package dataaccess;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO{
    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }

    @Override
    public AuthData createAuth() throws DataAccessException {
        return null;
    }

    @Override
    public boolean getAuth(String token) throws DataAccessException {
        return false;
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {

    }
}

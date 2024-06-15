import java.net.URI;
import java.net.URISyntaxException;

import dataaccess.DataAccessException;
import server.Server;

public class ServerFacade {
    int port;
    private static Server server;
    public ServerFacade() throws DataAccessException {
        try {
            this.port = server.run(0);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    //Connect Helper Functions
    public URI createURI(String path) throws URISyntaxException {
        return new URI("http://localhost:" + port + "/" + path);
    }
}

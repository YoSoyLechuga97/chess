import java.net.URI;
import java.net.URISyntaxException;

public class ServerFacade {
    int port;
    public ServerFacade(int port) {
        this.port = port;
    }

    //Connect Helper Functions
    public URI createURI(String path) throws URISyntaxException {
        return new URI("http://localhost:" + port + "/" + path);
    }
}

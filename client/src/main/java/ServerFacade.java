import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
    private static HttpURLConnection sendRequest(String url, String method, String body) throws URISyntaxException, IOException, IOException {
        URI uri = new URI(url);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        writeRequestBody(body, http);
        http.connect();
        System.out.printf("= Request =========\n[%s] %s\n\n%s\n\n", method, url, body);
        return http;
    }
    private static void writeRequestBody(String body, HttpURLConnection http) throws IOException {
        if (!body.isEmpty()) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }
}

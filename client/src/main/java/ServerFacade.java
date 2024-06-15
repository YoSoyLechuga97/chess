import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import server.Server;

public class ServerFacade {
    int port;
    String path;
    String method;
    String body;
    String url;
    public ServerFacade() throws DataAccessException {
        Server server1 = new Server();
        this.port = server1.run(0);
    }

    public void login(String username, String password) throws URISyntaxException, IOException {
        path = "session";
        url = createURL(path);
        body = "{ \"username\":\"" + username + "\", \"password\":\"" + password + "\" }";
        method = "POST";

        HttpURLConnection loginConnection = sendRequest(url, method, body);
        receiveResponse(loginConnection);
    }

    //Connect Helper Functions
    public String createURL(String path) throws URISyntaxException {
        return "http://localhost:" + port + "/" + path;
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
    private static void receiveResponse(HttpURLConnection http) throws IOException {
        var statusCode = http.getResponseCode();
        var statusMessage = http.getResponseMessage();

        if (statusCode == 200) {
            Object responseBody = readResponseBody(http);
            System.out.printf("= Response =========\n[%d] %s\n\n%s\n\n", statusCode, statusMessage, responseBody);
        } else {
            System.out.println("[" + statusCode + "] : " + statusMessage);
        }
    }

    private static Object readResponseBody(HttpURLConnection http) throws IOException {
        Object responseBody = "";
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            responseBody = new Gson().fromJson(inputStreamReader, Map.class);
        }
        return responseBody;
    }
}

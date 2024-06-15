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
import model.AuthData;
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

    public AuthData login(String username, String password) throws URISyntaxException, IOException {
        path = "session";
        url = createURL(path);
        body = "{ \"username\":\"" + username + "\", \"password\":\"" + password + "\" }";
        method = "POST";

        return sendAndRecieve();
    }

    public AuthData register(String username, String password, String email) throws URISyntaxException, IOException {
        path = "user";
        url = createURL(path);
        body = "{ \"username\":\"" + username + "\", \"password\":\"" + password + "\", \"email\":\"" + email + "\" }";
        method = "POST";

        return sendAndRecieve();
    }

    public AuthData sendAndRecieve() throws IOException, URISyntaxException {
        HttpURLConnection loginConnection = sendRequest(url, method, body);
        Object authObj = receiveResponse(loginConnection);
        if (authObj == null) {
            return null;
        }
        Map<String, String> authMap = (Map<String, String>) authObj;
        String authToken = authMap.get("authToken");
        String authName = authMap.get("username");
        return new AuthData(authToken, authName);
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
    private static Object receiveResponse(HttpURLConnection http) throws IOException {
        var statusCode = http.getResponseCode();
        var statusMessage = http.getResponseMessage();

        if (statusCode == 200) {
            return readResponseBody(http);
        } else {
            System.out.println("[" + statusCode + "] : " + statusMessage);
            return null;
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

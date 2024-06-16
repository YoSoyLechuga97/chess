package facade;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.ListGamesData;

public class ServerFacade {
    int port;
    String path;
    String method;
    String body;
    String url;
    String header = "";
    String headerValue = "";
    public ServerFacade(int port) throws DataAccessException {
        this.port = port;
    }

    public AuthData login(String username, String password) throws URISyntaxException, IOException {
        path = "session";
        url = createURL(path);
        header = "";
        headerValue = "";
        body = "{ \"username\":\"" + username + "\", \"password\":\"" + password + "\" }";
        method = "POST";

        return sendAndReceive(AuthData.class);
    }

    public AuthData register(String username, String password, String email) throws URISyntaxException, IOException {
        path = "user";
        url = createURL(path);
        header = "";
        headerValue = "";
        body = "{ \"username\":\"" + username + "\", \"password\":\"" + password + "\", \"email\":\"" + email + "\" }";
        method = "POST";

        return sendAndReceive(AuthData.class);
    }

    public AuthData logout(AuthData user) throws URISyntaxException, IOException {
        path = "session";
        url = createURL(path);
        header = "authorization";
        headerValue = user.authToken();
        body = "";
        method = "DELETE";

        return sendAndReceive(AuthData.class);
    }

    public void createGame(AuthData user, String gameName) throws URISyntaxException, IOException {
        path = "game";
        url = createURL(path);
        header = "authorization";
        headerValue = user.authToken();
        body = "{ \"gameName\":\"" + gameName + "\" }";
        method = "POST";

        sendAndReceive(null);
    }

    public ArrayList<GameData> listGames(AuthData user) throws URISyntaxException, IOException {
        path = "game";
        url = createURL(path);
        header = "authorization";
        headerValue = user.authToken();
        body = "";
        method = "GET";
        ListGamesData list = sendAndReceive(ListGamesData.class);
        if (list == null) {
            return null;
        }
        return list.games();
    }

    public void joinGame (AuthData user, String color, int gameID) throws URISyntaxException, IOException {
        path = "game";
        url = createURL(path);
        header = "authorization";
        headerValue = user.authToken();
        body = "{ \"playerColor\":\"" + color + "\", \"gameID\":" + gameID + " }";
        method = "PUT";

        sendAndReceive(null);
    }

    public <T> T sendAndReceive(Class<T> responseClass) throws IOException, URISyntaxException {
        HttpURLConnection loginConnection = sendRequest(url, method, body, header, headerValue);
        return receiveResponse(loginConnection, responseClass);
    }

    //Connect Helper Functions
    public String createURL(String path) throws URISyntaxException {
        return "http://localhost:" + port + "/" + path;
    }
    private static HttpURLConnection sendRequest(String url, String method, String body, String header, String headerValue) throws URISyntaxException, IOException, IOException {
        URI uri = new URI(url);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        if (!header.isEmpty()) {
            http.setRequestProperty(header, headerValue);
        }
        writeRequestBody(body, http);
        http.connect();
        //System.out.printf("= Request =========\n[%s] %s\n\n%s\n\n", method, url, body);
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
    private static <T> T receiveResponse(HttpURLConnection http, Class<T> responseClass) throws IOException {
        var statusCode = http.getResponseCode();
        var statusMessage = http.getResponseMessage();

        if (statusCode == 200) {
            return readResponseBody(http, responseClass);
        } else {
            System.out.println("[" + statusCode + "] : " + statusMessage);
            return null;
        }
    }

    private static <T> T readResponseBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
}

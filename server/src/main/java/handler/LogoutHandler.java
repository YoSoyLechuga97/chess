package handler;

import com.google.gson.Gson;
import exceptions.UnauthorizedException;
import model.AuthData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.Reader;
import java.util.Map;

public class LogoutHandler implements Route {
    private final UserService userService;
    private final Gson gson;

    public LogoutHandler() {
        this.userService = new UserService();
        this.gson = new Gson();
    }
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            String authToken = request.headers("authorization");
            AuthData authData = new AuthData(authToken, null);
            userService.logout(authData);
            return "";
        } catch (Exception e) {
            if (e instanceof UnauthorizedException) { //If logout fails
                response.status(401);
            } else {
                response.status(500);
            }
            //Message to return if logout fails
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}

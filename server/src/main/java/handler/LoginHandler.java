package handler;

import com.google.gson.Gson;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class LoginHandler implements Route {
    private final UserService userService;
    private final Gson gson;

    public LoginHandler() {
        this.userService = new UserService();
        this.gson = new Gson();
    }
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            UserData userData = gson.fromJson(request.body(), UserData.class);
            AuthData authData = userService.login(userData);
            return gson.toJson(authData);
        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                response.status(401);
            } else {
                response.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}

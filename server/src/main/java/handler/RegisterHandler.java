package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import exceptions.AlreadyExistsException;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class RegisterHandler implements Route {

    private final UserService userService;
    private final Gson gson;

    public RegisterHandler() {
        this.userService = new UserService();
        this.gson = new Gson();
    }
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            UserData userData = gson.fromJson(request.body(), UserData.class);
            AuthData authData = userService.register(userData);
            return gson.toJson(authData);
        } catch (Exception e) {
            if (e instanceof AlreadyExistsException) {
                response.status(403);
            } else if ( e instanceof JsonSyntaxException) {
                response.status(400);
            } else {
                response.status(500);
            }

            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}

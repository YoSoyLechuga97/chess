package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {

    private UserService userService;
    private Gson gson;

    public RegisterHandler() {
        this.userService = new UserService();
        this.gson = new Gson();
    }
    public String registerHandler (String jsonRequest) throws DataAccessException {
        try {
            UserData userData = gson.fromJson(jsonRequest, UserData.class);

            userService.register(userData);

            return "Success";
        } catch (JsonSyntaxException e) {
            return "Improper JSON format";
        }
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            UserData userData = gson.fromJson(request.body(), UserData.class);

            AuthData authData = userService.register(userData);

            return gson.toJson(authData);
        } catch (Exception e) {
            response.status(400);
            return "Improper JSON format";
        }
    }
}

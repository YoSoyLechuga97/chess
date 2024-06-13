package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import exceptions.AlreadyExistsException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.RequestData;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class JoinHandler implements Route {
    private final UserService userService;
    private final GameService gameService;
    private final Gson gson;

    public JoinHandler() {
        this.userService = new UserService();
        this.gameService = new GameService();
        this.gson = new Gson();
    }
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            String authToken = request.headers("authorization");
            AuthData authData = new AuthData(authToken, null);
            RequestData requestData = gson.fromJson(request.body(), RequestData.class);
            gameService.joinGame(authData, requestData.playerColor(), requestData.gameID());
            return "";
        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                response.status(401);
            } else if (e instanceof JsonSyntaxException) {
                response.status(400);
            } else if (e instanceof AlreadyExistsException) {
                response.status(403);
            } else {
                response.status(500);
            }

            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}

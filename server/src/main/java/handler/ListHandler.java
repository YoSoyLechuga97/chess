package handler;

import com.google.gson.Gson;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.ListGamesData;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.Map;

public class ListHandler implements Route {
    private final UserService userService;
    private final GameService gameService;
    private final Gson gson;

    public ListHandler() {
        this.userService = new UserService();
        this.gameService = new GameService();
        this.gson = new Gson();
    }
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            String authToken = request.headers("authorization");
            AuthData authData = new AuthData(authToken, null);
            ListGamesData allGames = gameService.listGames(authData);
            return gson.toJson(allGames);
        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {//Exception messages and their status codes
                response.status(401);
            } else {
                response.status(500);
            }
            //Print out the exception message
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}

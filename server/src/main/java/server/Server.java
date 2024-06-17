package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import handler.*;
import spark.*;

import java.sql.SQLException;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        //Connect to Websocket
        WSServer websocket = new WSServer();
        websocket.run(desiredPort);

        Spark.staticFiles.location("web");

        //Connect to Database and create tables
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            databaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", new RegisterHandler());
        Spark.post("/session", new LoginHandler());
        Spark.delete("/session", new LogoutHandler());
        Spark.post("/game", new CreateHandler());
        Spark.put("/game", new JoinHandler());
        Spark.get("/game", new ListHandler());
        Spark.delete("/db", new ClearHandler());

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import handler.*;
import spark.*;

import java.sql.SQLException;

public class Server {

    public int run(int desiredPort) throws DataAccessException {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        //Connect to Database and create tables
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.createDatabase();

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

package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    private static final ArrayList<String> SCHEMA = new ArrayList<>();

    private static final String AUTH_TABLE = """
            CREATE TABLE IF NOT EXISTS auth (
            authToken VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken)
            )""";

    private static final String USER_TABLE = """
            CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            PRIMARY KEY (username)
            )""";

    private static final String GAME_TABLE = """
            CREATE TABLE IF NOT EXISTS game (
            gameID INT NOT NULL,
            whiteUsername VARCHAR(255) DEFAULT NULL,
            blackUsername VARCHAR(255) DEFAULT NULL,
            gameName VARCHAR(255) NOT NULL,
            game longtext NOT NULL,
            PRIMARY KEY (gameID)
            )""";

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to load db.properties");
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
                SCHEMA.add(AUTH_TABLE);
                SCHEMA.add(USER_TABLE);
                SCHEMA.add(GAME_TABLE);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    public void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
            conn.setCatalog(DATABASE_NAME);
            //Create Tables
            for (String table : SCHEMA) {
                try (var createTableStatement = conn.prepareStatement(table)){
                    createTableStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void addAuth(String authToken, String username) throws DataAccessException {
        createDatabase();
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)){
            conn.setCatalog(DATABASE_NAME);
            try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES(?, ?)")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException((e.getMessage()));
        }
    }

    public void addUser(String username, String password, String email) throws DataAccessException {
        createDatabase();
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)){
            conn.setCatalog(DATABASE_NAME);
            try (var preparedStatement = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES(?, ?, ?)")) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException((e.getMessage()));
        }
    }

    public void addGame(int gameID, String gameName, ChessGame game) throws DataAccessException {
        createDatabase();
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);){
            conn.setCatalog(DATABASE_NAME);
            try (var preparedStatement = conn.prepareStatement("INSERT INTO game (gameID, gameName, game) VALUES(?, ?, ?)")) {
                preparedStatement.setInt(1, gameID);
                preparedStatement.setString(2, gameName);
                var json = new Gson().toJson(game);
                preparedStatement.setString(3, json);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException((e.getMessage()));
        }
    }


    public String findData(String table, String key, String returnType, String inputSearch) throws DataAccessException {
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)){
            conn.setCatalog(DATABASE_NAME);
            try (var preparedStatement = conn.prepareStatement("SELECT " + returnType + " FROM " + table + " WHERE " + key + " = ?")) {
                preparedStatement.setString(1, inputSearch);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getString(returnType);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException((e.getMessage()));
        }
    }

    public void updateData(String table, String updateRow, String update, String keyType, String key) throws DataAccessException {
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)){
            conn.setCatalog(DATABASE_NAME);
            try (var preparedStatement = conn.prepareStatement("UPDATE " + table + " SET " + updateRow + "=? WHERE " + keyType + "=?")) {
                preparedStatement.setString(1, update);
                preparedStatement.setString(2, key);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException((e.getMessage()));
        }
    }

    public ArrayList<GameData> listGames() throws DataAccessException{
        ArrayList<GameData> allGames = new ArrayList<>();
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)){
            conn.setCatalog(DATABASE_NAME);
            try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game")) {
                ResultSet resultSet = preparedStatement.executeQuery();

                //Combine to GameData object
                while (resultSet.next()) {
                    int gameID = resultSet.getInt("gameID");
                    String white = resultSet.getString("whiteUsername");
                    String black = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
                    String gameString = resultSet.getString("game");
                    ChessGame game = new Gson().fromJson(gameString, ChessGame.class);
                    allGames.add(new GameData(gameID, white, black, gameName, game));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException((e.getMessage()));
        }
        return allGames;
    }

    public void deleteData(String table, String keyName, String key) throws DataAccessException {
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)){
            conn.setCatalog(DATABASE_NAME);
            try (var preparedStatement = conn.prepareStatement("DELETE FROM " + table + " WHERE " + keyName + "=?")) {
                preparedStatement.setString(1, key);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException((e.getMessage()));
        }
    }

    public void clearTable(String table) throws DataAccessException{
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)){
            conn.setCatalog(DATABASE_NAME);
            try (var preparedStatment = conn.prepareStatement("DROP TABLE IF EXISTS " + table)) {
                preparedStatment.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException((e.getMessage()));
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}

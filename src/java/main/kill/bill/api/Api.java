package kill.bill.backend.api;

import io.javalin.Javalin;
import kill.bill.backend.database.DbConnect;

import java.sql.Connection;
import java.sql.SQLException;

public class Api {
    private static Connection connection;

    public static void main(String[] args) {
        try {
            DbConnect dbConnect = new DbConnect("jdbc:sqlite:other_testing.db");
            connection = dbConnect.connect();
            dbConnect.createWorldTable(connection);

            APIHandler.setConnection(connection);

            Javalin app = Javalin.create();

            app.get("/world/{worldName}", APIHandler::getWorld);
            app.get("/world", APIHandler::getAllWorlds);

            app.start(7000);

            app.events(event -> {
                event.serverStopped(() -> closeConnection());
            });

        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }

    private static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}

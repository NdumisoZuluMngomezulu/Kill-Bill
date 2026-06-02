package kill.bill.backend.api;

import io.javalin.http.Context;
import kill.bill.backend.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Handler {
    private static Connection connection;

    public static void setConnection(Connection conn) {
        connection = conn;
    }

    public static void getWorld(Context ctx) {
        String worldName = ctx.pathParam("worldName");

        try {
            DatabaseConnection dbConnect = new DatabaseConnection("");
            GameEnviroDb world = dbConnect.getWorldByName(connection, worldName);

            if (world != null) {
                ctx.json(world);
            } else {
                ctx.status(404).contentType("application/json").result("{\"message\": \"World not found\"}");
            }
        } catch (SQLException e) {
            ctx.status(500).contentType("application/json").result(
                    "{\"message\": \"Error fetching world: " + e.getMessage() + "\"}");
        }
    }

    public static void getAllWorlds(Context ctx) {
        try {
            DatabaseConnection dbConnect = new DatabaseConnection("");
            List<GameEnviroDb> worlds = dbConnect.getAllSavedWorlds(connection);
            ctx.json(worlds);
        } catch (SQLException e) {
            ctx.status(500).contentType("application/json").result(
                    "{\"message\": \"Error fetching worlds: " + e.getMessage() + "\"}");
        }
    }
}

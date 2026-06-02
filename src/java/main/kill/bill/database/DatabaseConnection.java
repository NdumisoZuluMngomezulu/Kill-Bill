package kill.bill.backend.database;

//imports
import org.communication.server.Server;
import org.communication.server.serverHelpers.world.World;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    
    public static String URL = "jdbc:sqlite:test_gameEnviro.db";

    public DatabaseConnection(String url) {
        URL = url;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public void createWorldTable(final Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS worlds (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "worldName TEXT NOT NULL, " +
                "size INTEGER NOT NULL, " +
                "obstacles TEXT" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'worlds' created or already exists.");
        } catch (SQLException e) {
            throw new SQLException("Failed to create the table 'worlds'.", e);
        }
    }


    public void createData( final Connection connection, String name_of_the_world, int size, String obstacles)
            throws SQLException
    {
        try( final PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO worlds(worldName, size, obstacles) VALUES (?, ?, ?)"
        )){
            stmt.setString( 1, name_of_the_world );
            stmt.setInt( 2, size );
            stmt.setString( 3, obstacles );
            final boolean gotAResultSet = stmt.execute();

            if( gotAResultSet ){
                throw new RuntimeException( "Unexpectedly got a SQL resultset." );
            }else{
                final int updateCount = stmt.getUpdateCount();
                if( updateCount == 1 ){
                    System.out.println( "1 row INSERTED into worlds" );
                }else{
                    throw new RuntimeException( "Expected 1 row to be inserted, but got " + updateCount );
                }
            }
        }
    }

    public void readData(final Connection connection) throws SQLException {
        String sql = "SELECT * FROM worlds";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name_of_world = rs.getString("worldName");
                int size = rs.getInt("size");
                String obstacles = rs.getString("obstacles");

                Server.worldSize = size;
                Server.obsCoor = obstacles;
                String[] coordList = obstacles.split(",");
                int xValue = Integer.parseInt(coordList[0]);
                int yValue = Integer.parseInt(coordList[1]);
                Object newObstacle = GameEnviro.createObstacle(xValue, yValue);
                GameEnviro.obstacles.add(newObstacle);

//                System.out.println("ID: " + id);
//                System.out.println("Name of World: " + name_of_world);
//                System.out.println("Size: " + size);
//                System.out.println("Obstacles: " + obstacles);
//                System.out.println("----------");
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to read data from 'worlds'.", e);
        }
    }

    public GameEnviro getWorldByName(Connection connection, String worldName) throws SQLException {
        String sql = "SELECT * FROM worlds WHERE worldName = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, worldName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    GameEnviro world = new GameEnviro();
                    world.setWorldName(rs.getString("worldName"));
                    world.setSize(rs.getInt("size"));
                    world.setObstacles(rs.getString("obstacles"));
                    return world;
                }
            }
        }
        return null;
    }

    public List<GameEnviro> getAllSavedWorlds(Connection connection) throws SQLException {
        List<GameEnviro> worlds = new ArrayList<>();
        String sql = "SELECT * FROM worlds";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                GameEnviro world = new GameEnviro();
                world.setWorldName(rs.getString("worldName"));
                world.setSize(rs.getInt("size"));
                world.setObstacles(rs.getString("obstacles"));
                worlds.add(world);
            }
        }
        return worlds;
    }

}

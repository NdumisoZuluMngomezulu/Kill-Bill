package kill.bill.backend.database;


/**
 * Data Transfer Object (DTO)
 * DTO class that represents the data from your SQLite database table.
 * **/
public class GameEnviro {
    // Attributes
    private String worldName;
    private int size;
    private String obstacles;

    // Getters and Setters
    public String getEnviroName() {
        return worldName;
    }

    public void setEnviroName(String worldName) {
        this.worldName = worldName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getObstacles() {
        return obstacles;
    }

    public void setObstacles(String obstacles) {
        this.obstacles = obstacles;
    }
}

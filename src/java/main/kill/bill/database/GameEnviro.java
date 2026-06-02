package kill.bill.backend.database;



public class GameEnviro {
    
    private String worldName;
    private int size;
    private String obstacles;

    
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

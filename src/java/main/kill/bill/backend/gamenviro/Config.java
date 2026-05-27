package kill.bill.backend.gamenviro;

public class Config {
    private final int topLeftX = 0;
    private final int topLeftY = 0;
    private final int bottomRightX = height - 1;
    private final int bottomRightY = width - 1;
    private int width;
    private int height;
    private int visibilityRange;
    private int maxPlayers;
    private int maxBullets;
    private int maxHealth;
    private int repairTime;
    private int respawnTime;
    private boolean respawnEnabled;

    public Config(int width, int height, int maxPlayers, int maxHealth, boolean respawnEnabled) {
        this.width = width;
        this.height = height;
        this.maxPlayers = maxPlayers;
        this.maxHealth = maxHealth;
        this.respawnenabled = respawnEnabled;
    }

    public int getTopLeftX() {
        return topLeftX;
    }

    public int getTopLeftY() {
        return topLeftY;
    }

    public int getBottomRightX() {
        return bottomRightX;
    }

    public int getBottomRightY() {
        return bottomRightY;
    }

    public int getWidth() {
        return width;
    }

    public void createEnvironment() {
        // Logic to create the game environment based on the configuration
    }
}

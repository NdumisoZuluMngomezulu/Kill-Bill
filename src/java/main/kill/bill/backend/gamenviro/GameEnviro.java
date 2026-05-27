package kill.bill.backend.gamenviro;

import kill.bill.backend.state.Position;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import kill.bill.backend.npc.DynamicNPC;
import kill.bill.backend.npc.StaticNPC;
import kill.bill.backend.person.Person;

public class GameEnviro {
    private final Position topLeft = new Position(0, 0);
    private final Position bottomRight;
    private final Position center;
    private HashMap<Person, Position> playerPositions = new ConcurrentHashMap<>();
    private HashMap<Position, DynamicNPC> dynamicNPCs = new ConcurrentHashMap<>();
    private HashMap<Position, StaticNPC> staticNPCs = new ConcurrentHashMap<>();
    private List<Person> deadPlayers = new ArrayList<>();
    private List<Person> activePlayers = new ArrayList<>();
    private int maxPlayers;
    private int maxHealth;
    private int visibilityRange;
    private int repairTime;
    private int respawnTime;
    private boolean respawnEnabled;


    public GameEnviro(Config config) {
        // Logic to initialize the game environment based on the configuration
        this.bottomRight = new Position(config.getBottomRightX(), config.getBottomRightY());
        this.center = new Position((topLeft.getX() + bottomRight.getX()) / 2, (topLeft.getY() + bottomRight.getY()) / 2);
        this.maxPlayers = config.getMaxPlayers();
        this.maxHealth = config.getMaxHealth();
        this.visibilityRange = config.getVisibilityRange();
        this.repairTime = config.getRepairTime();
        this.respawnTime = config.getRespawnTime();
        this.respawnEnabled = config.isRespawnEnabled();

    }

    public void addPlayer(Person player) {
        if (!activePlayers.contains(player) && activePlayers.size() < maxPlayers) {
            activePlayers.add(player);
            this.playerPositions.put(player, player.getState().getPosition());
        } else {
            System.out.println("Cannot add player: " + player.getName() + ". Maximum players reached or player already exists.");
        }
        // Logic to add a player to the game environment
    }

    public void removePlayer(Person player) {
        activePlayers.remove(player);
        playerPositions.remove(player);
        // Logic to remove a player from the game environment
    }

    public boolean respawnPlayer(Person player) {
        if (respawnEnabled && deadPlayers.contains(player)) {
            player.setHealth(maxHealth);
            player.setPosition(center);
            activePlayers.add(player);
            deadPlayers.remove(player);
            return true;
        }
        return false;
    }

}

package kill.bill.backend.Utilities;

import java.util.List;
import java.util.ArrayList;


public class State {
    private Position position;
    private Direction direction;
    private int health;
    private int score;
    private List<String> inventory;

    public State(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
        this.health = 100;
        this.score = 0;
        this.inventory = new ArrayList<>();
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getHealth() {
        return health;
    }

    public int getScore() {
        return score;
    }

    public List<String> getInventory() {
        return inventory;
    }
}

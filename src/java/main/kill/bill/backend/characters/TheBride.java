package kill.bill.backend.commands;

public class TheBride extends Person {
    int damage;
    
    public TheBride(String name, int health) {
        super(name, health);
    }

    @Override
    public void attack(Person target) {
        // Implement attack logic for The Bride
    }
}
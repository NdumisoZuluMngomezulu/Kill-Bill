package kill.bill.backend.characters;

public class ElleDriver extends Person {
    int damage;
    
    public ElleDriver(String name, int health) {
        super(name, health);
    }

    @Override
    public void attack(Person target) {
        // Implement attack logic for O-Ren
    }
    
}

package kill.bill.backend.characters;

public class Bill extends Person {
    int damage;
    
    public Bill(String name, int health) {
        super(name, health);
    }

    @Override
    public void attack(Person target) {
        // Implement attack logic for O-Ren
    }
    
}
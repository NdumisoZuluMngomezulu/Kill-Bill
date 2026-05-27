package kill.bill.backend.characters;

public class JohnnyMo extends Person {
    int damage;

    public JohnnyMo(String name, int health) {
        super(name, health);
    }

    @Override
    public void attack(Person target) {
        // Implement attack logic for Johnny Mo
    }
}

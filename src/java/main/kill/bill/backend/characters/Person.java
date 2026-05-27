package kill.bill.backend.characters;

public abstract class Person {
    private String name;
    private int health;

    public Person(String name, int health) {
        this.name = name;
        this.health = health;
    }

    public String getName() {return name;}
    public int getHealth() {return health;}

    public abstract void attack(Person target);

}

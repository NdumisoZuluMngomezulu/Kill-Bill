package kill.bill.backend.characters;

import kill.bill.backend.state.State;

public abstract class Person {
    private String name;
    private int health;
    private State state;

    public Person(String name, int health) {
        this.name = name;
        this.health = health;
    }

    public String getName() {return name;}
    public int getHealth() {return health;}

    public abstract void attack(Person target);
    
    public State getState() {return state;}
    public void setState(State state) {this.state = state;}
}

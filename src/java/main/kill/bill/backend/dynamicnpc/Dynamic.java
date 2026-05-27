package kill.bill.backend.dyanamicnpc;

public abstract class DynamicNPC {
    private String name;
    private int health;

    public DynamicNPC(String name, int health) {
        this.name = name;
        this.health = health;
    }

    public String getName() {return name;}
    public int getHealth() {return health;}

    public abstract void performAction(DynamicNPC target);

}
public kill.bill.backend.commands.Shoot 

public Shoot extends Command {
    
    public Shoot() {
        super("shoot");
    }

    @Override
    public boolean execute(Person target) {
        // Implement the logic for the punch command here
        // For example, you could reduce the target's health or apply a status effect
        System.out.println("Punching " + target.getName());
        return true; // Return true if the command was executed successfully
    }
}
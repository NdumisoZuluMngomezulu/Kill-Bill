package kill.bill.backend.commands;

import kill.bill.backend.characters.Person;

public class Kick extends Command {
    public Kick() {
        super("kick");
    }

    @Override
    public boolean execute(Person target) {
        // Implement the logic for the kick command here
        // For example, you could reduce the target's health or apply a status effect
        System.out.println("Kicking " + target.getName());
        return true; // Return true if the command was executed successfully
    }
}
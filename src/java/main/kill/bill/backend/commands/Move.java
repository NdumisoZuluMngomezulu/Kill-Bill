package kill.bill.backend.commands;

import kill.bill.backend.characters.Person;

public class Move extends Command {
    public Move(String direction) {
        super("move", direction);
    }

    @Override
    public boolean execute(Person target) {
        // Implement the logic for the move command here
        System.out.println("Moving ");
        return true; // Return true if the command was executed successfully
    }
}
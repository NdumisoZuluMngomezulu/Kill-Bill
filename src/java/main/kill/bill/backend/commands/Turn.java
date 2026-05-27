package kill.bill.backend.commands;

public class Turn extends Command {
    public Turn(String direction) {
        super("turn", direction);
    }

    @Override
    public boolean execute() {
        // Implement the logic for the turn command here
        System.out.println("Turning ");
        return true; // Return true if the command was executed successfully
    }
}
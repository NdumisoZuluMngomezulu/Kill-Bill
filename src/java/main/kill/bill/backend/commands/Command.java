package kill.bill.backend.commands;
/**
 * Represents an abstract command that can be executed on a robot.
 * Provides basic properties and methods for command execution.
 */

import kill.bill.backend.characters.*;
import kill.bill.backend.utilities.*;


public abstract class Command {
    private final String name;
    private String argument;

    public abstract boolean execute(Person target);

    public Command(String name){
        this.name = name.trim().toLowerCase();
        this.argument = "";
    }

    public Command(String name, String argument) {
        this(name);
        this.argument = argument.trim();
    }

    public String getName() {
        return name;
    }

    public String getArgument() {
        return argument;
    }


    public static Command create(String instruction) {

        if (instruction == null || instruction.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty instruction");
        }

        String[] parts = instruction.trim().split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1] : "";

        return switch (command) {
            case "move" -> new Move(arg);
            case "shoot"    -> new Fire();
            case "punch"  -> new Punch();
            case "kick"   ->  new Kick();
            case "look"    -> new LookCommand(arg);
            case "turn"    -> new TurnCommand(arg);
            case "reload"  -> new ReloadCommand(arg);
            case "repair"  -> new RepairCommand(arg);
            default -> throw new IllegalArgumentException("Unsupported command: " + instruction);
        };
    }

}

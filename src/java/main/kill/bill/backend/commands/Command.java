package kill.bill.backend.commands;

import kill.bill.backend.characters.Person;
/**
 * Represents an abstract command that can be executed on a robot.
 * Provides basic properties and methods for command execution.
 */
public abstract class Command {
    private final String name;
    private String argument;

    public abstract boolean execute(Person target);

    public Command(String name){
        this.name = name.trim().toLowerCase();
        this.argument = "";
    }
    //constructs a command with a specified name and argument
    public Command(String name, String argument) {
        this(name);
        this.argument = argument.trim();
    }
    public String getName() {                                                                    
        return name;
    }

    public String getArgument() {
        return this.argument;
    }

    /**
     * Creates a specific Command object based on the provided instruction.
     * @param instruction The instruction string specifying the command to create.
     * @return A Command object corresponding to the given instruction.
     * @throws IllegalArgumentException If the instruction does not match any supported command.
     */
    public static Command create(String instruction) {

        String[] args = instruction.toLowerCase().trim().split(" ");

        return switch (args[0]) {
            case "move" -> new Move(args[1]);
            case "look" -> new Look(args[0]);
            case "fire" -> new Shoot();
            case "reload" -> new Reload();
            case "pickup" -> new Pickup();
            case "turn" -> new TurnCommand(args[1]);
            case "kick" -> new Kick();
            case "punch" -> new Punch();
            default -> throw new IllegalArgumentException("Unsupported command: " + instruction);
        };
    }
}


package kill.bill.frontend;

public class Request {
    private String name;
    private String command;
    private String[] args;

    public Request(String name, String command, String[] args) {
        this.name = name;
        this.command = command;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }
}

package kill.bill.frontend;

public class RequestToServer {
    private String name;
    private String command;
    private String[] args;

    public RequestToServer(String name, String command, String[] args) {
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

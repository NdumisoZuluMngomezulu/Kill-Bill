package kill.bill.frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import kill.bill.backend.menu.Menu;
import kill.bill.backend.gameenviro.GameEnviro;
import kill.bill.backend.characters.Person;
import kill.bill.backend.characters.Bill;
import kill.bill.backend.characters.ElleDriver;
import kill.bill.backend.characters.TheBride;
import kill.bill.backend.characters.Oren;
import kill.bill.backend.characters.JohhnyMo;
import kill.bill.backend.characters.HattoriHanzo;



/**
 * The SimpleClientHandler class handles client interactions, including
 * sending responses to the client, processing JSON data, and managing a list
 * of robot models.
 */
public class ServerHandler {

    Scanner sc = new Scanner(System.in);
    private PrintStream out;
    private Gson gson;
    private static ArrayList<String> robotModels;
    private boolean robotLaunched;
    /**
     * Constructs a SimpleClientHandler with the specified PrintStream for output,
     * and initializes the Gson object and the list of robot models.
     */
    public ServerHandler(PrintStream out, Gson gson, ArrayList<String> robotModels) {
        this.out = out;
        this.gson = gson;
        this.robotModels = robotModels;
        this.robotLaunched = false;
    }

    /**
 * Handles user input commands from the scanner and processes them accordingly.
 * Commands include launching actions and handling valid commands based on input parts.
 */
    public void handleUserInput(Scanner sc) {
        while (Client.keepRunning) {
            String input = sc.nextLine();
            String[] commandArray = input.toLowerCase().split(" ");

            if (input.contains("quit")){
                String[] args = {};
                Request request = new Request("client", "quit", args);
                out.println(gson.toJson(request));
                out.flush();
                System.out.println("GAME CANCELED");
                System.exit(0);
            }

            try {
                System.out.println(Arrays.deepToString(commandArray));
                if (commandArray[0].toLowerCase().equals("launch") && commandArray.length > 2){
                    handleLaunchCommand(commandArray);
                } else if (robotLaunched){
                    if (SimpleServerHandler.validCommands.contains(commandArray[0])){
                        handleValidCommand(commandArray);
                    } else{displayInvalidCommandMessage(input);
                }                       
                }else {
                    System.out.println("Please launch a robot to start!");
                }
            } catch (Exception e) {
                displayInvalidCommandMessage(input);
            }
        }
    }

    /**
 * Handles the launch command specified by the user input parts.
 * throws and excpeption if there's an error during robot instantiation or method invocation.
 */
    private void handleLaunchCommand(String[] parts) throws Exception {
        //Check if the robot model exists in the list of robot models and if launch is allowed
        if (robotModels.contains(parts[1]) && Client.conditionForLaunch) {
            //Disable further launches
            Client.conditionForLaunch = false;
            robotLaunched = true; // Set the flag to indicate a robot has been launched
            // Create an instance of the robot
            int shield;
            int shots;
            switch (parts[1]){
                case "sniper":
                    Person robot = new TheBride("sniper");
                    shield = robot.getShields();
                    shots = robot.getShots();
                    break;
                case "warrior":
                    Person bot = new ElleDriver("warrior");
                    shield = bot.getShields();
                    shots = bot.getShots();
                    break;
                case "blaster":
                    Person obot = new Oren("blaster");
                    shield = obot.getShields();
                    shots = obot.getShots();
                    break;
                default:
                    shield = 15;
                    shots = 15;
            }
            String shi = String.valueOf(shield);
            String sho = String.valueOf(shots);
            // If robot instance is successfully created
            if (shield > 0) {
                // Invoke methods to get shield and shots information using reflection
                
                String[] stringArgs = {parts[1], shi, sho};

                // Create a RequestToServer object with robot name, command, and arguments
                RequestToServer request = new RequestToServer(parts[2], parts[0], stringArgs);
                out.println(gson.toJson(request));
                out.flush();
            }
        } else {
            // Print error message if launch is not allowed or robot model is invalid
            System.out.println("You have already launched or invalid robot model!" );
        }
    }

    /**
 * Handles a valid command based on the user input parts.
 * parts of the user input command.
 */
    public void handleValidCommand(String[] parts) {
        // Create a new RequestToServer object
        RequestToServer request = new RequestToServer();

        // Set the command for the RequestToServer object
        request.setCommand(parts[0]);

        // Check if there is exactly one argument provided and it is not a valid command
        if (parts.length == 2 && SimpleServerHandler.validCommands.contains(parts[0])) {
            // Set the argument for the RequestToServer object
            request.setArguments(new String[]{parts[1]});
        }
        // Convert RequestToServer object to JSON and send it the server
        out.println(gson.toJson(request));
        out.flush();
    }

    /**
 * Creates an instance of a robot based on the provided model name.
 */
    public Object createPersonInstance(String model) throws Exception {
        if (model.toLowerCase().equals("sniper")){
            return new TheBride("sniper");
        } else if (model.toLowerCase().equals("warrior")){
            return new ElleDriver("warrior");
        } else if (model.toLowerCase().equals("blaster")){
            return new Oren("blaster");
        } else {
            System.err.println("Invalid robot model: " + model);
            return null;
        }
    }          

    /**
 * Displays an invalid command message for the given user input.
 * If the user input starts with "help", it invokes the help menu method from the Client class.
 * @param userInput The user input that resulted in an invalid command.
 */
    private void displayInvalidCommandMessage(String userInput) {
        // Print an error message indicating invalid command or arguments
        System.out.println("Invalid Command or Arguments. Try again or enter 'help'" );

        // Check if user input starts with "help"
        if (userInput.startsWith("help")) {
            Menu.viewMenu();
        }
    }

    /**
 * Handles responses received from the server and processes them for client
 */
    /*
    used in handleServerResponse to display message to client
    */
    public void displayServerResponse(String serverResponse){
        if (serverResponse == null || serverResponse.trim().isEmpty()) {
            System.out.println("Received an empty or null JSON response.");
            return;
        }

         try {
            Gson gson = new Gson();
            JsonElement jsonElement = gson.fromJson(serverResponse, JsonElement.class);

            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                JsonObject data = jsonObject.has("data") ? jsonObject.getAsJsonObject("data") : null;
                JsonObject state_info = jsonObject.has("state") ? jsonObject.getAsJsonObject("state") : null;

                if (data != null) {
                    String jsonString = gson.toJson(data);
                    System.out.println(jsonString);}
                if (state_info != null) {
                    String jsonString = gson.toJson(state_info);
                    System.out.println(jsonString);}
                
            } else {
                System.out.println(serverResponse);
            }
        } catch (JsonSyntaxException e) {
            System.out.println("Failed to parse JSON: " + e.getMessage());
        }
    }
    public void handleServerResponse(BufferedReader in) throws IOException {
        String serverResponse;
        while((serverResponse = in.readLine()) != null){
            //TO
            //Cater to quit, shot, pit, too many of you in this world
            if (serverResponse.contains("quit")){
                System.out.println("Server has shutdown the game");
                Client.keepRunning = false;
                System.exit(0);
                break;

            } else if(serverResponse.contains("jikeleza")){
                Response response = gson.fromJson(serverResponse, Response.class);
                Map<String, String> data = response.getStringData();
                String direction = data.get("jikeleza");
                System.out.println("You are now facing :" + direction);
                    // String reloadTime;
            } else if (serverResponse.contains("repairTIME") || serverResponse.contains("reloadTIME")) {
                System.out.println("Your robot is frozen! Waiting...");
                try {
                    int secondsToWait = 5000; // Default fallback fallback
                    Response response = gson.fromJson(serverResponse, Response.class);

                    // 3. Extract the value from the data map
                    // Map<String, String> data = response.getStringData();
                    // String reloadTime;
                    // if (serverResponse.contains("repairTIME")){
                    //     reloadTime = data.get("reloadTIME");
                    // } else{reloadTime = data.get("repairTIME");}
                    

                    // // 4. Convert to integer if needed
                    // secondsToWait = Integer.parseInt(reloadTime);
                
                    // Sleep the thread (Thread.sleep takes milliseconds)
                    Thread.sleep(secondsToWait); 
                    System.out.println("Person unfrozen!");
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
        
            }else if (serverResponse.contains("DEAD")) {
                System.out.println("User has been shot DEAD");
                Client.keepRunning = false;
                System.exit(0);
                break;

            } else if (serverResponse.contains("emgodini")) {
                System.out.println("User has been fell into a pit");
                Client.keepRunning = false;
                System.exit(0);
                break;

            } else if (serverResponse.contains("halabaloo")) {
                System.out.println("Too many of you in this world. Restart the client and choose a different name.");
                Client.keepRunning = false;
                System.exit(0);
                break;

            } else if(serverResponse.contains("A player has joined the game.")){
                System.out.println("A player has joined the game.");
                System.out.println("\n Enter your next move");
            } else if(serverResponse.contains("A player has joined the game.")){
                System.out.println("A player has joined the game.");
                System.out.println("\n Enter your next move");
            } else if (serverResponse.contains("kliks")){
                System.out.println("Move successful.");
                System.out.println("\n Enter your next move");
            } else if (serverResponse.contains("blocked")){
                System.out.println("Position is obstructed.");
                System.out.println("\n Enter your next move");
            } else if (serverResponse.contains("This is what you see")){
                Map<String, Object> response = gson.fromJson(serverResponse, Map.class);

                // 2. Extract the inner data map
                Map<String, Object> data = (Map<String, Object>) response.get("data");

                // 3. Extract the lists (Gson parses JSON arrays into ArrayLists)
                ArrayList<String> obstacles = (ArrayList<String>) data.get("obstacles");
                ArrayList<String> robots = (ArrayList<String>) data.get("robots");
                System.out.println("======= This is what you see =======");
                // 4. Iterate and display
                for (String obs : obstacles) {
                    System.out.println("Obstacles: " + obs);
                }

                for (String botName : robots) {
                    System.out.println("Persons: " + botName);
                }
            }
            displayServerResponse(serverResponse);
            System.out.println("\n Enter your next move");
        }
            //Then prompt the player for their next move
            
        }
}
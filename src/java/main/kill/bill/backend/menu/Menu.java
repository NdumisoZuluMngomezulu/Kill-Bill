package za.co.wethinkcode.robots.helper.menu;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import za.co.wethinkcode.robots.SimpleServerHandler;
import za.co.wethinkcode.robots.helper.obstacle.Pit;
import za.co.wethinkcode.robots.helper.obstacle.BottomlessPit;
import za.co.wethinkcode.robots.helper.obstacle.Lake;
import za.co.wethinkcode.robots.helper.obstacle.Mountain;
import za.co.wethinkcode.robots.helper.obstacle.Obstacle;
import za.co.wethinkcode.robots.helper.robot.Position;
import za.co.wethinkcode.robots.helper.robot.Robot;
import za.co.wethinkcode.robots.helper.world.World;

public class Menu {

   public static ArrayList<Position> launchingSpots = new ArrayList<>();

   public Menu(){}
   /**
    * Displays the world commands menu.
    */
   public static void viewMenu(){
       System.out.println( "\nWorld Commands");
       System.out.println("'quit' - Disconnects all robots and ends the world ");
       System.out.println("'robots' - Lists all robots including the robot's name and state");
       System.out.println("'view' - displays all the available/acceptable commands");
       System.out.println("'dump' - Displays a representation of the worlds state\n");
   }

   /**
    * Displays the header for the robot connection.
    */

   /**
    * Displays the general header for the game.
    */
   public static void displayMenu(){
       System.out.println( "\n**********************************************************************");
       System.out.println("            🤖✨ We have war. We have worlds. We have robots! ✨🤖");
       System.out.println( "**********************************************************************");
       System.out.println("");
   }

    /**
     * 
    explains how the game works, the shooting sequence, the type of obstacles - how they work
   */

   public static void gameplayDescription() {

    System.out.println("╔════════════════════════════════════════════════════════════════════╗");
    System.out.println("║             ⚔⚔⚔⚔ KILL BILL — SURVIVE OR DIE TRYING ⚔⚔⚔⚔⚔        ║");
    System.out.println("╠════════════════════════════════════════════════════════════════════╣");

    System.out.println("║                                                                    ║");
    System.out.println("║                           🎯OBJECTIVE                              ║");
    System.out.println("║                                                                    ║");
    System.out.println("║  Be the last Player standing. Destroy all enemies by reducing       ║");
    System.out.println("║  their health to zero. No respawns. No mercy.                     ║");
    System.out.println("║                                                                    ║");

    System.out.println("╠════════════════════════════════════════════════════════════════════╣");

    System.out.println("║                                                                    ║");
    System.out.println("║                     🕹️  MOVEMENT & TURNING                         ║");
    System.out.println("║                                                                    ║");
    System.out.println("║  • forward <n>  - Move n steps in the direction you face           ║");
    System.out.println("║  • back <n>     - Move n steps backwards                           ║");
    System.out.println("║  • turn left    - Rotate 90° to the left                           ║");
    System.out.println("║  • turn right   - Rotate 90° to the right                          ║");
    System.out.println("║                                                                    ║");
    System.out.println("║  You cannot move through robots or solid obstacles.                ║");
    System.out.println("║  Moving out of bounds keeps you in place.                          ║");
    System.out.println("║                                                                    ║");

    System.out.println("╠════════════════════════════════════════════════════════════════════╣");

    System.out.println("║                                                                    ║");
    System.out.println("║                          ⚔️  ATTACKING                            ║");



    System.out.println("╠════════════════════════════════════════════════════════════════════╣");

    System.out.println("║                                                                    ║");
    System.out.println("║                   🛡️  HEALTH & ELIMINATION                         ║");
    System.out.println("║                                                                    ║");
    System.out.println("║                                                                    ║");

    System.out.println("╠════════════════════════════════════════════════════════════════════╣");

    System.out.println("║                                                                    ║");
    System.out.println("║                           🌍 OBSTACLES                             ║");
    System.out.println("║                                                                    ║");

    System.out.println("╠════════════════════════════════════════════════════════════════════╣");

    System.out.println("║                                                                    ║");
    System.out.println("║                           💻 COMMANDS                              ║");
    System.out.println("║                                                                    ║");
    System.out.println("║  • join  - Launch your robot into the world                      ║");
    System.out.println("║  • look    - See what surrounds your robot                         ║");
    System.out.println("║  • state   - View shields, ammo and status                         ║");
    System.out.println("║  • fire    - Attack in the direction you face                      ║");
    System.out.println("║  • quit    - Disconnect and end the session                        ║");
    System.out.println("║                                                                    ║");

    System.out.println("╠════════════════════════════════════════════════════════════════════╣");

    System.out.println("║                                                                    ║");
    System.out.println("║                            🏆 WINNING                              ║");
    System.out.println("║                                                                    ║");
    System.out.println("║  Eliminate every other player to win the game.                     ║");
    System.out.println("║                                                                    ║");
    System.out.println("║  → Use mountains for cover                                         ║");
    System.out.println("║  → Don't waste ammo too early                                      ║");
    System.out.println("║  → Stay away from Bottomless Pits                                  ║");
    System.out.println("║  → Learn your robot's range                                        ║");
    System.out.println("║                                                                    ║");

    System.out.println("╚════════════════════════════════════════════════════════════════════╝");
    System.out.println();

}

   /**
    * Displays a message indicating the server is running and waiting for connections.
    */
   public static void displayWaitingForConnections(){
       System.out.println( "\t\tServer running & waiting for client connections.");
       System.out.println( "----------------------------------------------------------------------");
   }



   /**
    * Displays the help menu with available commands.
    */
   public static void helpMenu() {
       // System.out.println( "\nHelp Menu" "shields", "Shields", );
       //                 printField(dataState, "shots", "Shots", );
       //                 printField(dataState);
       System.out.println( "'launch'- launch a new player into the world");
       System.out.println("'look'- Allows your player to look around");
       System.out.println("'state'- View the current state of your player\n");
       System.out.println("'forward' - move the player forward e.g forward 10 ");
       System.out.println("'back' - move the player backwards e.g back 50");
       System.out.println("'turn' - turn the player either left or right e.g turn left");
       System.out.println("'fire'  - shoot your shot");
   }

   /**
    * Displays the configuration menu for setting the world size.
    */
   public static void configMenu(){
       System.out.println( "\nLet's start by configuring the world size:");
       System.out.println( "Please choose a setting below:" );
   }


   /**
    * Displays the robot statistics and available characters.
    */
   public static void displayRobotStats() {
    System.out.println("╔════════════════════════════════════════════════════════════════════╗");
    System.out.println("║                      🎭 PLAYER SELECTION 🎭                       ║");
    System.out.println("╠════════════════════════════════════════════════════════════════════╣");

    System.out.println("║                                                                    ║");
    System.out.println("║    BILL ElleDriver   HattoriHanzo   JohnnyMo   Oren   TheBride     ║");
    System.out.println("║                                                                    ║");
    System.out.println();

    System.out.print("Choose your player (1-5): ");

   }

   /**
    * Displays the statistics for a specific robot.

   }

   /**
    * Displays the obstacles and robots present in the world.
    */
   public static void displayObstaclesAndPlayers(){
       
    }

   /**
    * Displays the list of robots with their details.
    */
   public static void listPlayers() {
       

   }
   /**
    * Displays the server response parsed from JSON format.
    *
    * @param serverResponse The server response in JSON format.
    */
   public static void displayServerResponse(String serverResponse) {
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



}
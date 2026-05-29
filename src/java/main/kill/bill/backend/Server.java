package kill.bill.backend;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import kill.bill.backend.menu.Menu;
import kill.bill.backend.gameenviro.GameEnviro;

/**
 * Handles multiple client connections and user interactions for the server.
 */

public class MultiServerHandler {

    private List<Socket> socketList;
    static public Map<String, SimpleServerHandler> activeClients = new ConcurrentHashMap<>();
    static public AtomicInteger clientCounter = new AtomicInteger(0);
    /**
     * Constructs a MultiServerHandler with a list of connected sockets.
     */
    public MultiServerHandler(List<Socket> socketList) {
        this.socketList = socketList;
    }


    /**
     * Starts a user input thread to handle interactive commands and operations.
     *
     * @param sc The Scanner object to read user input.
     */

    public void run(Scanner sc) {
        Thread serverThread = new Thread(() -> {
            System.out.println("Enter 'view' for available commands.");
            while (!Thread.currentThread().isInterrupted()) {
                if (sc.hasNextLine()) {
                    String userInput = sc.nextLine();
                    handleCommand(userInput);
                } else {
                    try {
                        Thread.sleep(1000); //prevent tightlooping
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Preserve interruption status
                    }
                }
            }
        });
        serverThread.start();
    }


    /**
     * Handles user commands by executing the corresponding actions.
     */
    private void handleCommand(String userInput) {

        switch (userInput) {
            case "view":
                Menu.viewMenu();
                break;
            case "robots":
                if ((SingleClientHandler.playerObjects.isEmpty())) {
                    System.out.println("Invalid, there aren't any robots connected!");
                } else {
                    Menu.listPlayers();
                    System.out.println();
                }
                break;
            case "dump":
                Menu.displayObstaclesAndPlayers();
                break;
            case "quit":
                Server.keepRunning = false;
                killClients();
                System.exit(0);
                break;
            default:
                System.out.println("Sorry I did not understand '" + userInput + "'. " +
                        "Enter 'view to see menu.");
        }
    }

    /**
     * Continuously accepts incoming client connections on the specified ServerSocket.
     * For each connection, creates a new SimpleServer task and starts it in a separate thread
     */
    public void acceptClients(ServerSocket serverSocket) {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                int currentCount = clientCounter.incrementAndGet();
                System.out.println("Someone has joined the game.");
                socketList.add(socket);
                String client = "Client" + currentCount;

                // Create a new task to handle the client connection
                SingleClientHandler TaskForEachClient = new SingleClientHandler(socket, this, client);
                activeClients.put(client, TaskForEachClient);
                // Start a new thread to execute the server task, unless termination flag is set
                if (Server.keepRunning) {
                    activeClients.put(client, TaskForEachClient);
                    Thread ThreadForEachClient = new Thread(TaskForEachClient);
                    ThreadForEachClient.start();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
    * Terminates all active connections by sending a "quit" command to each connected client.
    */
    private void killClients() {
        for (Socket eachSocket : socketList) {
            try (PrintStream out = new PrintStream(eachSocket.getOutputStream())) {
                out.println("quit");
                out.flush();
            } catch (IOException e) {
                System.out.println("Terminating players...");
            }
        }

        for (Socket eachSocket : socketList) {
            try {
                eachSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method used by one thread to find and notify another thread
    public void routeShotNotification(String targetId, String shooterId) {
        SingleClientHandler targetHandler = activeClients.get(targetId);
        if (targetHandler != null) {
            targetHandler.sendHItNotification(shooterId);
        }
    }

    /**
     * Configuring the world boundaries according to the x and y inputs
     */
    public void createWorld(int x, int y) {
        Config.setTopLeftX_world(0);
        Config.setTopLeftY_world(0);
        Config.setBottomRightX_world(x-1);
        Config.setBottomRightY_world(y-1);
    }

}

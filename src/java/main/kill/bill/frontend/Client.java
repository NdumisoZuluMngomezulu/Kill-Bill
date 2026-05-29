package kill.bill.frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;

import kill.bill.backend.menu.Menu;

//Represents a client application with state variables related to its operation.

public class Client {
     
    public static boolean keepRunning = true;
    public static boolean conditionForLaunch = true;

    /**
 * Main entry point for the client application.
 * Establishes a connection to the server, initializes necessary components,
 * and manages user input and server responses using a multi-threaded approach.
 */
    public static void main(String[] args) throws IOException {
        String ipAddress = "localhost";
        int port = 5000;
        Scanner sc = new Scanner(System.in);
        Gson gson = new Gson();
        ArrayList<String> robotModels = new ArrayList<>(Arrays.asList("sniper", "warrior", "blaster"));

        for (int i = 0; i < args.length; i+=2) {
            switch (args[i]) {
                case "-p" :
                    port = Integer.parseInt(args[i+1]);
                    break;
                case "-s" :
                    ipAddress = args[i+1];
                    break;
            }
        }
        try (
                // Establish socket connection with the server, and initialize input and output streams
                Socket socket = new Socket(ipAddress, port);
                PrintStream out = new PrintStream(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            Menu.displayMenu();
            Menu.displayRobotStats();
            System.out.println("You are client number : " + MultiServerHandler.clientCounter);
            System.out.println("Please launch in this order: ");
            System.out.println("Launch RobotType Name");
            System.out.println("Launch RobotType Name + Client number e.g: launch sniper sanele");

            // Create instance of SimpleClientHandler to handle communication with the server
            SimpleClientHandler handler = new SimpleClientHandler(out, gson, robotModels);

            // starts a new, separate thread in Java to handle 
            // user input without freezing the main application
            Thread inputThread = new Thread(() -> handler.handleUserInput(sc)); //This is a lambda expression acting as a Runnable. 
            // It tells the new thread what to do when it runs. 
            // It calls the handleUserInput method on a handler object, passing in a scanner
            inputThread.start();
            /*
*inputThread.start() launches the thread. 
It tells the Java Virtual Machine (JVM) to start a new, concurrent thread of execution 
and call the run() method (defined by the lambda) within that new thread.
*Crucial Difference: Do not use inputThread.run() instead of .start(). 
Calling run() directly will run the code in the same thread, not a new one, freezing our entry point */

            handler.handleServerResponse(in); //in is the bufferedreader object taking in input

            inputThread.interrupt();
/*
*a method in Java used to politely request that a thread stop what it is doing. 
It does not force the thread to terminate immediately, but rather sets an internal "interrupted" flag 
and triggers specific behaviors depending on the thread's current state.  

Here is a breakdown of what happens when Thread.interrupt() is called:
1. Behavior Based on Thread State
If the thread is blocked (sleeping, waiting, or joining): The thread wakes up immediately, the interrupt status is cleared, and an InterruptedException is thrown.
If the thread is performing active work: The thread continues running, but an internal boolean "interrupted" flag is set to true. The thread must check this flag periodically to see if it should stop.
If the thread is blocked in I/O operations (NIO): The channel is closed, the flag is set, and a ClosedByInterruptException is thrown.
*/

        } catch (IOException e) {
            // Handle connection-related exceptions
            if (e instanceof ConnectException) {
                System.out.println("No Server found... Please run the server!");
            } else {
                e.printStackTrace();
            }
        }
    }

}

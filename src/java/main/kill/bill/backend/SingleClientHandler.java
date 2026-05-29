package kill.bill.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import kill.bill.backend.Request;
import kill.bill.backend.GameEnviro;
import kill.bill.backend.Response;
import kill.bill.backend.State;
import kill.bill.backend.Command;
import kill.bill.backend.Direction;
import kill.bill.backend.Person;
import kill.bill.backend.staticnpc.StaticNpc;


public class SingleClientHandler implements Runnable{
    public Socket clientSocket;
    public BufferedReader in;
    public PrintStream out;
    public String clientId;
    Gson gson;
    
    public SingleClientHandler(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintStream(socket.getOutputStream());
        clientId = socket.getRemoteSocketAddress().toString();
        gson = new Gson();
    }

    public void run(){
        GameEnviro world = GameEnviro.getInstance();


        String messageFromClient;
        try {
            while ((messageFromClient = in.readLine()) != null) {
                try {
                    Request request = gson.fromJson(messageFromClient, Request.class);
                    String command = request.getCommand();
                    if (player != null){
                        for (Person bot : GameEnviro.player_list){
                            if (bot.getName().equals(player.getName())){
                                if (bot.getShields() <= 0){
                                    GameEnviro.player_list.remove(bot);
                                    GameEnviro.player_coord.remove(bot);
                                    listOfPersonSockets.remove(bot.getName());
                                    playerObjects.remove(bot);
                                    out.println(deadResponseToClient(player, gson, 0, 0));
                                }
                            }
                        }
                    }

                    if (command.equals("launch")) {
                        System.out.println("Handling launch");
                        player = handleLaunch(request, world);
                    } else if(command.equals("quit")){
                        handleQuit(player, world, gson);
                    } else if(command.equals("forward")){
                        switch (player.getCurrentDirection()){
                            case NORTH:
                                incrementY(player, world, command, request.getArguments());
                                break;
                            case SOUTH:
                                decrementY(player, world, command, request.getArguments());
                                break;
                            case EAST:
                                decrementX(player, world, command, request.getArguments());
                                break;
                            case WEST:
                                incrementX(player, world, command, request.getArguments());
                                break;
                            default:
                                incrementY(player, world, command, request.getArguments());
                                break;
                        }

                    } else if(command.equals("back")){
                        switch (player.getCurrentDirection()){
                            case NORTH:
                                decrementY(player, world, command, request.getArguments());
                                break;
                            case SOUTH:
                                incrementY(player, world, command, request.getArguments());
                                break;
                            case EAST:
                                incrementX(player, world, command, request.getArguments());
                                break;
                            case WEST:
                                decrementX(player, world, command, request.getArguments());
                                break;
                            default:
                                decrementY(player, world, command, request.getArguments());
                                break;
                        }
                    }else if(command.equals("turn")){
                        if (request.getArguments()[0].equals("left")){
                            System.out.println("Turning left");
                            handleLeft(player, world, command, request.getArguments());
                        } else {
                            System.out.println("Handling right");
                            handleRight(player, world, command, request.getArguments());
                        }
                    } else if (validCommands.contains(command)){   
                        if (player == null) {
                            simpleResponse(null, gson, "ERROR", "Person not found");
                        }

                        switch (command) {
                            case "look" -> handleLook(world, player);
                            case "direction" -> handleOrientation(player);
                            case "state" -> handleState(player);
                            case "fire" -> handleFire(player, gson, world);
                            case "reload" -> handleReload(player, world);
                            case "repair" -> handleRepair(player, world);
                            default -> handleState(player);
                        }
                    }
                } catch (IllegalArgumentException | NullPointerException e) {
                    String simpleResponse = simpleResponse(player, gson, "ERROR", "Could not execute");
                    out.println(simpleResponse);
                } catch (JsonSyntaxException ignored) {
                    System.out.println("Invalid JSON format");
                }
            }
        } catch (IOException e) {
            if (e instanceof SocketException) {
                System.out.println("A player has been terminated... bye");
            } else {e.printStackTrace();}
        }
    }

    public void handleQuit(Person bot, GameEnviro world, Gson gson){
        try {
            MultiServerHandler.activeClients.remove(bot.getName());
        } catch (Exception e) {
        }
        GameEnviro.player_list.remove(bot);
        listOfPersonSockets.remove(bot.getName());
        playerObjects.remove(bot);
        Response response = new Response();
        HashMap<String, String> info_map = new HashMap<>();
        String setResult = "OK";
        info_map.put(bot.getName(), "Has Left The Game");
        response.setResult(setResult);
        State state = new State(bot.getPosition(), bot.getCurrentDirection(), 0, 0);
        state.setStatus("LEFT");
        response.setState(state);
        out.println(gson.toJson(response));

        if (playerObjects.isEmpty()){
            System.out.println("No more players left in this world.");
        }
    }

    public Person createPersonInstance(String model){
        if (model.toLowerCase().equals("sniper")){
            return new Sniper("sniper");
        } else if (model.toLowerCase().equals("warrior")){
            return new Warrior("warrior");
        } else if (model.toLowerCase().equals("blaster")){
            return new Blaster("blaster");
        } else {
            System.err.println("Invalid player model: " + model);
            return null;
        }
    }   

    private Person handleLaunch(Request request, GameEnviro world) {
        String name = request.getPersonName();
        System.out.println("Name " + name);
        String type = request.getArguments()[0];
        System.out.println("Type: " + type);

        if(playerNames.contains(name)){
            out.println(simpleResponse(null, gson, "ERROR", "halabaloo"));
            
            return null;
        } else{
            playerNames.add(name);
            Person player = createPersonInstance(type);
            player.setName(request.getPersonName());
            player.setType(type);
            playerNames.add(player.getName());
            Random random = new Random();
            
            int x = random.nextInt((Server.x));
            int y = random.nextInt((Server.y));
            Position pos = new Position(x, y);
            boolean condition = false;
            while (condition = false){
                if (world.isPositionFree(pos) && world.isInBounds(pos)){
                    condition = true;
                } else{
                    int a = random.nextInt((Server.x));
                    int b = random.nextInt((Server.y));
                    pos = new Position(a, b);
                    condition = false;
                }
            }
            player.setPosition(pos);

            GameEnviro.player_list.add(player);
            listOfPersonSockets.put(name, this.socket);
            GameEnviro.player_coord.put(player, player.getPosition());
            GameEnviro.client_map.put(clientID, player);
            playerObjects.add(player);

            int shield = Integer.parseInt(request.getArguments()[1]);
            int shots = Integer.parseInt(request.getArguments()[2]);
            System.out.printf("%s has joined the game at position x: %d, y: %d", name, player.getPosition().getPosition_x(), player.getPosition().getPosition_y());
            out.println(sendResponsetoClient(player, gson, shield, shots));

            return player;
        } 
    }

    private void incrementX(Person player, GameEnviro world, String command, String[] args){
        int new_x = player.getPosition().getPosition_x() + Integer.parseInt(args[0]);
        int new_y = player.getPosition().getPosition_y();
        
        Position newPosition = new Position(new_x, new_y);
        if (world.isInBounds(newPosition) && world.isPositionFree(newPosition)){
            for (int i = player.getPosition().getPosition_y(); i <= new_x; i++){
                Position pos = new Position(i, new_y);
                if (world.isInBounds(pos) && world.isPositionFree(pos)){
                    continue;
                } else {
                    if (world.isPit(pos) || world.isBottomlessPit(pos)){
                        GameEnviro.player_list.remove(player);
                        GameEnviro.player_coord.remove(player);
                        listOfPersonSockets.remove(player.getName());
                        playerObjects.remove(player);
                        System.out.println(player.getName()+" emgodini");
                        out.println(pitResponseToClient(player, gson, 0, 0));
                        break;
                    }
                    System.out.println("Blocked. The new Position is");                                
                    //Command cmd = Command.create("forward");
                    int distance = i - player.getPosition().getPosition_y();
                    world.updatePosition(player, pos, "forward", distance);
                    player.updatePos(i - player.getPosition().getPosition_y(), "right");
                    String text = "You have moved forwards to " + i + " kliks";
                    out.println(movement(world, player, gson, text));
                    break;
                }
            }
            Command cmd = Command.create("forward");
            int change = new_y - player.getPosition().getPosition_y();
            world.updatePosition(player, newPosition, "forward", change);
            player.updatePos(new_y - player.getPosition().getPosition_y(), "right");
            String message = "You have moved forwards to " + new_x + " kliks";
            out.println(movement(world, player, gson, message));
        } else {
            System.out.println("Position blocked");
            String message = "Position blocked.";
            out.println(movement(world, player, gson, message));
        }
        //send unsuccessful or error response
    }

    private void decrementX(Person player, GameEnviro world, String command, String[] args){
        int new_x = player.getPosition().getPosition_x() - Integer.parseInt(args[0]);
        int new_y = player.getPosition().getPosition_y();
        Position newPosition = new Position(new_x, new_y);
        if (world.isInBounds(newPosition) && world.isPositionFree(newPosition)){
            for (int i = player.getPosition().getPosition_y(); i >= new_x; i--){
                Position pos = new Position(i, new_y);
                if (world.isInBounds(pos) && world.isPositionFree(pos)){
                    continue;
                } else {
                    if (world.isPit(pos) || world.isBottomlessPit(pos)){
                        GameEnviro.player_list.remove(player);
                        GameEnviro.player_coord.remove(player);
                        listOfPersonSockets.remove(player.getName());
                        playerObjects.remove(player);
                        System.out.println(player.getName()+" emgodini");
                        out.println(pitResponseToClient(player, gson, 0, 0));
                        break;
                    }
                    System.out.println("Blocked. The new Position is");
                    //Command cmd = Command.create("forward");
                    int distance = player.getPosition().getPosition_y() - i;
                    world.updatePosition(player, pos, "backward", distance);
                    player.updatePos(player.getPosition().getPosition_y() - i, "left");
                    String text = "You have moved forwards to " + i + " kliks";
                    out.println(movement(world, player, gson, text));
                    break;
                }
            }
            Command cmd = Command.create("forward");
            int change = player.getPosition().getPosition_y() - new_x;
            world.updatePosition(player, newPosition, "backward", change);
            player.updatePos(player.getPosition().getPosition_y() - new_x, "left");
            String message = "You have moved backwards to " + new_x + " kliks";
            out.println(movement(world, player, gson, message));
        } else {
            System.out.println("Position blocked");
            String message = "Position blocked.";
            out.println(movement(world, player, gson, message));
        }
        //send unsuccessful or error response
    }

    private void incrementY(Person player, GameEnviro world, String command, String[] args){
        int new_x = player.getPosition().getPosition_x();
        int new_y = player.getPosition().getPosition_y() + Integer.parseInt(args[0]);
        
        Position newPosition = new Position(new_x, new_y);
        if (world.isInBounds(newPosition) && world.isPositionFree(newPosition)){
            for (int i = player.getPosition().getPosition_y(); i <= new_y; i++){
                Position pos = new Position(new_x, i);
                if (world.isInBounds(pos) && world.isPositionFree(pos)){
                    continue;
                } else {
                    if (world.isPit(pos) || world.isBottomlessPit(pos)){
                        GameEnviro.player_list.remove(player);
                        GameEnviro.player_coord.remove(player);
                        listOfPersonSockets.remove(player.getName());
                        playerObjects.remove(player);
                        System.out.println(player.getName()+" emgodini");
                        out.println(pitResponseToClient(player, gson, 0, 0));
                        break;
                    }
                    System.out.println("Blocked. The new Position is");
                    Command cmd = Command.create("forward");
                    int distance = i - player.getPosition().getPosition_y();
                    world.updatePosition(player, pos, "forward", distance);
                    player.updatePos(i - player.getPosition().getPosition_y(), "forward");
                    String text = "You have moved forwards to " + i + " kliks";
                    out.println(movement(world, player, gson, text));
                    break;
                }
            }
            Command cmd = Command.create("forward");
            int distance = new_y - player.getPosition().getPosition_y();
            world.updatePosition(player, newPosition, "forward", distance);
            player.updatePos(new_y - player.getPosition().getPosition_y(), "forward");
            String message = "You have moved forwards to " + new_y + " kliks";
            out.println(movement(world, player, gson, message));
        } else {
            System.out.println("Position blocked");
            String message = "Position blocked.";
            out.println(movement(world, player, gson, message));
        }
        //send unsuccessful or error response
    }

    private void decrementY(Person player, GameEnviro world, String command, String[] args){
        int new_x = player.getPosition().getPosition_x();
        int new_y = player.getPosition().getPosition_y() - Integer.parseInt(args[0]);
        
        Position newPosition = new Position(new_x, new_y);
        if (world.isInBounds(newPosition) && world.isPositionFree(newPosition)){
            for (int i = player.getPosition().getPosition_y(); i >= new_y; i--){
                Position pos = new Position(new_x, i);
                if ( world.isInBounds(pos) && world.isPositionFree(pos)){
                    continue;
                } else {
                    if (world.isPit(pos) || world.isBottomlessPit(pos)){
                        GameEnviro.player_list.remove(player);
                        GameEnviro.player_coord.remove(player);
                        listOfPersonSockets.remove(player.getName());
                        playerObjects.remove(player);
                        System.out.println(player.getName()+" emgodini");
                        out.println(pitResponseToClient(player, gson, 0, 0));
                        break;
                    }
                    System.out.println("Blocked. The new Position is");
                    Command cmd = Command.create("forward");
                    int distance = player.getPosition().getPosition_y() - i;
                    world.updatePosition(player, pos, "backward", distance);
                    player.updatePos(player.getPosition().getPosition_y() - i, "backward");
                    String text = "You have moved backwards to " + i + " kliks";
                    String message = simpleResponse(player, gson, command, text);
                    System.out.println(message);
                    out.println(message);
                    break;
                }
            }
            Command cmd = Command.create("forward");
            int change = player.getPosition().getPosition_y() - new_y;
            world.updatePosition(player, newPosition, "backward", change);
            player.updatePos(player.getPosition().getPosition_y() - new_y, "backward");
            String message = "You have moved backwards to " + new_y + " kliks";
            out.println(movement(world, player, gson, message));
        } else {
            System.out.println("Position blocked");
            String message = "Position blocked.";
            out.println(movement(world, player, gson, message));
        }
        //send unsuccessful or error response
    }

    private void handleLeft(Person player, GameEnviro world, String command, String[] args){
        world.changeBotDirectionLeft(player);
        switch(player.getCurrentDirection()){
            case NORTH:
                player.setDirection(Direction.EAST);
                break;
            case SOUTH:
                player.setDirection(Direction.WEST);
                break;
            case EAST:
                player.setDirection(Direction.SOUTH);
                break;
            case WEST:
                player.setDirection(Direction.NORTH);
                break;
            default:
                player.setDirection(Direction.EAST);
        }
        String message = "Turning Left. Type & Enter State to find your new direction";
        String response = simpleResponse(player, gson,"OK", message);
        out.println(response);
    }

    private void handleRight(Person player, GameEnviro world, String command, String[] args){
        world.changeBotDirectionRight(player);
        switch(player.getCurrentDirection()){
            case NORTH:
                player.setDirection(Direction.WEST);
                break;
            case SOUTH:
                player.setDirection(Direction.EAST);
                break;
            case EAST:
                player.setDirection(Direction.NORTH);
                break;
            case WEST:
                player.setDirection(Direction.SOUTH);
                break;
            default:
                player.setDirection(Direction.WEST);
        }
        String message = "Turning Right. Type & Enter State to find your new direction";
        String response = simpleResponse(player, gson,"OK", message);
        out.println(response);
    }



    private void handleLook(GameEnviro world, Person player) {
        System.out.println("Looking");
        // Command look = Command.create("look");
        // player.handleCommand(look);
        //String json_string = lookToJson(player, gson, "OK" , player.getState().getShields(), player.getState().getShots());
        String json_string = lookToJson(world, player, gson, "OK", player.getShields(), player.getShots());
        out.println(json_string);
    }

    /**
     * Converts look response to a JSON string that has information about staticNpcs.
     */
    private String lookToJson(GameEnviro world, Person player, Gson gson, String setResult, int shield, int shots) {
        Response response = new Response();
        Map<String, Object> info_map = new HashMap<>();
        ArrayList<String> staticNpc_list = new ArrayList<>();
        ArrayList<String> player_list = new ArrayList<>();
        //String key;

        if (!GameEnviro.staticNpc_list.isEmpty()){
            for (StaticNpc obs : GameEnviro.staticNpc_list){
                if (world.isStaticNpcInRange(player, obs)){staticNpc_list.add(obs.toString());}}
        }
        if (GameEnviro.player_list.size() > 1){
            for (Person bot : GameEnviro.player_list){
                if (!bot.getName().equals(player.getName())){
                    player_list.add(bot.getName());}
                }
                
        }
        String message = "This is what you see";
        info_map.put("message", message);
        info_map.put("staticNpcs", staticNpc_list);
        info_map.put("players", player_list);
        response.setData(info_map);
        response.setResult(setResult);

        System.out.println(gson.toJson(response));

        return gson.toJson(response);
    }

    private void handleFire(Person player, Gson gson, GameEnviro world) {
        Command fire = Command.create("fire");
        player.handleCommand(fire);
        String message;

        if (Fire.damagedPerson != null){
            message = sendFireResponseHit(Fire.damagedPerson, player, gson,
                player.getShields(), player.getShots());
            world.decreaseShots(player);
            world.decreaseShields(Fire.damagedPerson);
        } else{
            message = sendFireResponseMiss(gson, player
                ,player.getShots());
            world.decreaseShots(player);
        }

        out.println(message);
    }

    private String sendFireResponseMiss( Gson gson, Person player,int shots){
        Map<String, Object> data = new HashMap<>();
        Response response = new Response();
        State state = new State(player.getPosition(), player.getCurrentDirection(),player.getShots(),player.getShields());
        data.put("message", "Miss");

        data.put("shots", state.getShots());
        response.setState(state);
        response.setData(data);

        return gson.toJson(response);

    }

    private String sendFireResponseHit(Person hitPerson, Person player, Gson gson, int shields, int shots) {
        Map<String, Object> data = new HashMap<>();
        Response response = new Response();

        response.setResult("OK");
        data.put("message", "Hit");
        data.put("player", hitPerson.getName());
        Map<String, Object> hitPersonState = new HashMap<>();
        hitPersonState.put("position", hitPerson.getPosition());
        hitPersonState.put("direction", hitPerson.getCurrentDirection());
        if (hitPerson.getState().getShields() == 0){
            hitPerson.getState().setStatus("DEAD");
        }
        hitPersonState.put("shields", hitPerson.getShields());
        hitPersonState.put("shots", hitPerson.getShots());
        hitPersonState.put("status", hitPerson.getStatus());
        data.put("state", hitPersonState);

        response.setData(data);
        State state = new State(player.getPosition(), player.getCurrentDirection(),player.getShots(),player.getShields());
        state.setPosition(player.getPosition());
        state.setDirection(player.getCurrentDirection());
        state.setStatus("NORMAL");
        response.setState(state);
        return gson.toJson(response);
    }


    /**
     * Handles the orientation command by sending the orientation response to the client.
     */
    private void handleOrientation(Person player) {
        out.println(OrientationResponse(player, gson));
    }

    /**
     * Sends an orientation response to the client.
     * return JSON string response containing the player's orientation and status.
     */
    private String OrientationResponse(Person player, Gson gson) {
        Response response = new Response();

        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("direction", player.getCurrentDirection());
        infoMap.put("status", player.getState().getStatus());
        response.setData(infoMap);
        response.setResult("OK");

        return gson.toJson(response);
    }
    
    private void handleState(Person player) {
        out.println(sendStateResponseToClient(player, gson, player.getShields(),
                player.getShots()));
    }

    private void handleRepair(Person player, GameEnviro world) {
        Command reload = Command.create("repair");
        boolean outcome = reload.execute(player);

        world.repair(player);
        player.repair();
        
        Response response = new Response();
        response.setResult("Repair");
        Map<String, Object> data = new HashMap<>();
        Map<String, String> info = new HashMap<>();
        info.put("repairTIME","repair");
        data.put("message", "Reloading Complete!");
        State state = new State(player.getPosition(),player.getCurrentDirection(),player.getShields(), player.getShots());
        state.setPosition(player.getPosition());
        state.setDirection(player.getCurrentDirection());
        response.setState(state);
        response.setData(data);
        response.setStringData(info);
        out.println(gson.toJson(response));
        out.flush();
    }
        

    private void handleReload(Person player, GameEnviro world) {
        Command reload = Command.create("reload");
        boolean outcome = reload.execute(player);
        world.reload(player);
        player.reload();

        Response response = new Response();
        response.setResult("Reload");
        Map<String, Object> data = new HashMap<>();
        Map<String, String> info = new HashMap<>();
        info.put("reloadTIME","RELOAD");
        data.put("message", "Reloading Complete!");
        State state = new State(player.getPosition(),player.getCurrentDirection(),player.getState().getShields(), player.getState().getShots());
        state.setPosition(player.getPosition());
        state.setDirection(player.getCurrentDirection());
        response.setState(state);
        response.setData(data);
        response.setStringData(info);
        out.println(gson.toJson(response));
        out.flush();
    }
    

    private String sendResponsetoClient(Person player, Gson gson, int shield, int shots) {
        Response response = new Response();
        State state = new State(player.getPosition(), player.getCurrentDirection(), shield, shots);
        state.setPosition(player.getPosition());
        state.setDirection(player.getCurrentDirection());
        player.setState(state);
        state.setStatus("NORMAL");
        response.setState(state);

        response.setResult("OK");
        Map<String, Object> data = new HashMap<>();
        data.put("position", player.getPosition());
        data.put("visibility", ConfigGameEnviro.visibility);
        data.put("reload", "5 seconds");
        data.put("repair", "5 seconds");
        data.put("shields", player.getState().getShields());
        response.setData(data);
        return gson.toJson(response);
    }

    /**
     * Sends a general response to the client containing player's status and other details.
     */
    private String sendStateResponseToClient(Person player, Gson gson, int shield, int shots) {
        Response response = new Response();
        State state = new State(player.getPosition(), player.getCurrentDirection(), shield, shots);
        state.setPosition(player.getPosition());
        state.setDirection(player.getCurrentDirection());
        response.setState(state);
        return gson.toJson(response);
    }

    private String deadResponseToClient(Person player, Gson gson, int shield, int shots) {
        Response response = new Response();
        State state = new State(player.getPosition(), player.getCurrentDirection(), shield, shots);
        state.setPosition(player.getPosition());
        state.setDirection(player.getCurrentDirection());
        state.setStatus("DEAD");
        response.setState(state);
        return gson.toJson(response);
    }

    private String pitResponseToClient(Person player, Gson gson, int shield, int shots) {
        Response response = new Response();
        State state = new State(player.getPosition(), player.getCurrentDirection(), shield, shots);
        state.setPosition(player.getPosition());
        state.setDirection(player.getCurrentDirection());
        state.setStatus("pit");
        response.setState(state);
        return gson.toJson(response);
    }

    /**
     * Sends a simplified response
     */
    private String simpleResponse(Person player, Gson gson, String setResult, String message) {
        Response response = new Response();
        response.setResult(setResult);
        Map<String, Object> data = new HashMap<>();
        data.put("message", message);
        response.setData(data);
        return gson.toJson(response);
    }

    private String movement(GameEnviro world, Person player, Gson gson, String message) {
        Response response = new Response();
        Map<String, String> info_map = new HashMap<>();
        
        info_map.put("message", message);
        response.setStringData(info_map);
        response.setResult("Done");

        System.out.println(gson.toJson(response));

        return gson.toJson(response);
    }

}


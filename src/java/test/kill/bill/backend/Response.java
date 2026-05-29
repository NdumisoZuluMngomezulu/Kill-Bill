package kill.bill.backend;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import kill.bill.backend.menu.Menu;
import kill.bill.backend.gameenviro.GameEnviro;
import kill.bill.backend.characters.Person;
import kill.bill.backend.utilities.Position;
import kill.bill.backend.utilities.Direction;
import kill.bill.backend.utilities.State;



@DisplayName("Response Integration Tests")
public class ResponseTest {
    private Gson gson;
    @BeforeEach
    void setUp(){
        gson = new Gson();
    }

    @Test
    @DisplayName("response: result, data, and state survive round-trip")
    void response_fullPipeline_allFieldsPreserved(){

        Response response = new Response();
        response.setResult("OK");

        Map<String, Object> data = new HashMap<>();
        data.put("message", "You have forward to 5 kliks");
        data.put("position", "5,5");

        response.setData(data);

        State state = new State(new Position(5,5), Direction.NORTH, 15,3);

        state.setStatus("NORMAL");
        response.setState(state);

        String json = gson.toJson(response);
        Response parsed = gson.fromJson(json,Response.class);

        assertEquals("OK", parsed.getResult());
        assertNotNull(parsed.getData());
        assertNotNull(parsed.getState());

    }

    @Test
    @DisplayName("response: error result preserved")
    void response_errorResult_preserved(){

        Response response = new Response();

        response.setResult("ERROR");

        Map<String, Object> data = new HashMap<>();
        data.put("message", "Could not execute");

        response.setData(data);

        String json = gson.toJson(response);
        Response parsed = gson.fromJson(json, Response.class);

        assertEquals("ERROR", parsed.getResult());
    }

    @Test
    @DisplayName("response:  shields and shots preserved")
    void response_stateValues_preserved(){

        Response response = new Response();

        State state = new State(new Position(0,0), Direction.EAST, 60, 4);

        response.setState(state);

        String json = gson.toJson(response);
        Response parsed = gson.fromJson(json, Response.class);

        assertEquals(60, parsed.getState().getShots());
        assertEquals(0, parsed.getState().getShields());
    }

    @Test
    @DisplayName("response: null data serialises safely")
    void response_nullData_serialisesSafely(){

        Response response = new Response();
        response.setResult("OK");

        assertDoesNotThrow(()-> gson.toJson(response));
    }

    @Test
    @DisplayName("pipeline: server response parseable by client")
    void pipeline_serverResponseParseableByClient(){

        Response response = new Response();

        response.setResult("OK");

        Map<String, Object> data = new HashMap<>();
        data.put("message", "You have moved forward to 5 kliks");

        response.setData(data);

        State state = new State(new Position(5, 10), Direction.NORTH, 15, 3);

        response.setState(state);

        String json = gson.toJson(response);

        JsonObject parsed = gson.fromJson(json, JsonObject.class);

        assertTrue(parsed.has("result"));
        assertEquals("OK", parsed.get("result").getAsString());
    }


}

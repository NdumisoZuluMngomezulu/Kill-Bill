package kill.bill.frontend;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import kill.bill.backend.menu.Menu;
import kill.bill.backend.gameenviro.GameEnviro;
import kill.bill.backend.characters.Person;
import kill.bill.backend.characters.Bill;
import kill.bill.backend.characters.ElleDriver;
import kill.bill.backend.characters.TheBride;
import kill.bill.backend.characters.Oren;
import kill.bill.backend.characters.JohhnyMo;
import kill.bill.backend.characters.HattoriHanzo;



public class ClientHandlerTest {

    private SimpleClientHandler handler;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        Gson gson = new Gson();
        ArrayList<String> personModels = new ArrayList<>(
                Arrays.asList("thebride", "hattorihanzo", "oren")
        );
        handler = new SimpleClientHandler(printStream, gson, personModels);
    }

    @Test
    void testCreateRobotInstance_thebride() throws Exception {
        Object person = handler.createRobotInstance("thebride");
        assertNotNull(person);
        assertInstanceOf(HattoriHanzo.class, person);
    }

    @Test
    void testCreateRobotInstance_hattorihanzo() throws Exception {
        Object person = handler.createRobotInstance("hattorihanzo");
        assertNotNull(person);
        assertInstanceOf(TheBride.class, person);
    }

    @Test
    void testCreateRobotInstance_oren() throws Exception {
        Object person = handler.createRobotInstance("oren");
        assertNotNull(person);
        assertInstanceOf(Oren.class, person);
    }

    @Test
    void testCreateRobotInstance_invalidModel() throws Exception {
        Object person = handler.createRobotInstance("invalidmodel");
        assertNull(person);
    }

    @Test
    void testCreateRobotInstance_caseInsensitive() throws Exception {
        Object person = handler.createRobotInstance("SNIPER");
        assertNotNull(person);
        assertInstanceOf(HattoriHanzo.class, person);
    }

    @Test
    void testDisplayServerResponse_doesNotCrashOnNull() {
        assertDoesNotThrow(() -> handler.displayServerResponse(null));
    }

    @Test
    void testDisplayServerResponse_doesNotCrashOnEmpty() {
        assertDoesNotThrow(() -> handler.displayServerResponse(""));
    }

    @Test
    void testDisplayServerResponse_doesNotCrashOnInvalidJson() {
        assertDoesNotThrow(() -> handler.displayServerResponse("this is not json"));
    }

    @Test
    void testDisplayServerResponse_validJsonWithData() {
        String json = "{\"data1\":{\"message\":\"Done\"},\"state\":{\"shots\":3}}";
        assertDoesNotThrow(() -> handler.displayServerResponse(json));
        String output = outputStream.toString();
        assertFalse(output.contains("shots"));
    }
}
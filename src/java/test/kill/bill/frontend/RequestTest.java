package kill.bill.frontend;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RequestTest {

    @Test
    void testConstructorWithNameOnly() {
        RequestToServer request = new RequestToServer("HAL");
        assertEquals("HAL", request.getRobotName());
    }

    @Test
    void testConstructorWithNameAndCommand() {
        RequestToServer request = new RequestToServer("HAL", "fire");
        assertEquals("HAL", request.getRobotName());
        assertEquals("fire", request.getCommand());
    }

    @Test
    void testConstructorWithNameCommandAndArgs() {
        String[] args = {"sniper", "1", "5"};
        RequestToServer request = new RequestToServer("HAL", "launch", args);
        assertEquals("HAL", request.getRobotName());
        assertEquals("launch", request.getCommand());
        assertArrayEquals(args, request.getArguments());
    }

    @Test
    void testSettersAndGetters() {
        RequestToServer request = new RequestToServer();
        request.setRobot("HAL");
        request.setCommand("forward");
        request.setArguments(new String[]{"5"});

        assertEquals("HAL", request.getRobotName());
        assertEquals("forward", request.getCommand());
        assertArrayEquals(new String[]{"5"}, request.getArguments());
    }

    @Test
    void testLaunchRobotReturnsJson() {
        RequestToServer request = new RequestToServer();
        String json = request.launchRobot("HAL", "launch");

        assertNotNull(json);
        assertTrue(json.contains("HAL"));
        assertTrue(json.contains("launch"));
    }

    @Test
    void testCommandRobotReturnsJson() {
        RequestToServer request = new RequestToServer();
        String[] args = {"5"};
        String json = request.commandRobot("HAL", "forward", args);

        assertNotNull(json);
        assertTrue(json.contains("HAL"));
        assertTrue(json.contains("forward"));
        assertTrue(json.contains("5"));
    }

    @Test
    void testEmptyConstructor() {
        RequestToServer request = new RequestToServer();
        assertNull(request.getRobotName());
        assertNull(request.getCommand());
        assertNull(request.getArguments());
    }
}
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestClientHandlers {
    private Client client;

    @BeforeEach
    void setUp() throws URISyntaxException {
        client = new Client(new URI("i/am/not/a/real/address"), List.of("BTC-USD"), 10);
    }

    @Test
    public void testUnhandledMessageType() throws URISyntaxException, IOException {
        String unhandled = TestUtils.readFileFromPath("json/unhandled.json");
        assertThrows(
                IllegalArgumentException.class,
                () -> client.onMessage(unhandled),
                "Expected to throw for unhandled message type"
        );
    }

    @Test
    public void testSubscriptionsMsgHandler() throws URISyntaxException, IOException {
        String subscriptions = TestUtils.readFileFromPath("json/subscriptions.json");
        client.onMessage(subscriptions);
        List<String> expectedList = List.of("BTC-USD");
        assertEquals(expectedList, client.getSubscriptions());
    }

    @Test
    public void testErrorMsgHandler() throws URISyntaxException, IOException {
        String error = TestUtils.readFileFromPath("json/error.json");
        assertThrows(
                RuntimeException.class,
                () -> client.onMessage(error),
                "Expected to throw for error message type"
        );
    }
}

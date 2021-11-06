import utils.MessageBuildUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMessageBuildUtils {
    private final JsonParser parser = new JsonParser();

    @Test
    public void testBuildSubscribeMsg() throws URISyntaxException, IOException {
        List<String> product_ids = List.of("ETH-USD", "ETH-EUR");
        List<String> channels = List.of("level2", "heartbeat");
        String subscribeMsg = TestUtils.readFileFromPath("json/subscribe-simple.json");
        String jsonString = MessageBuildUtils.createSubscribeMessage(product_ids, channels);

        JsonElement expected = parser.parse(subscribeMsg);
        JsonElement actual = parser.parse(jsonString);
        assertEquals(expected, actual);
    }
}

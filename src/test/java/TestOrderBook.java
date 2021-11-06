import messages.SnapshotMsg;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestOrderBook {
    private final OrderBook orderBook = new OrderBook("BTC-USD");
    private final Gson gson = new Gson();

    @Test
    public void test() throws URISyntaxException, IOException {
        String snapshotJson = TestUtils.readFileFromPath("json/snapshot-large.json");
        SnapshotMsg snapshotMsg = gson.fromJson(snapshotJson, SnapshotMsg.class);
        orderBook.consumeSnapshot(snapshotMsg);
        String expectedToContain = "Asks: 45605.46=0.04069328, 45607.78=0.01498385, 45607.94=0.02941897, 45608.09=0.13, 45609.12=0.05306974";
        assertTrue(orderBook.toString(10).contains(expectedToContain));
    }
}

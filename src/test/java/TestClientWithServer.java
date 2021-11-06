import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.MessageBuildUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestClientWithServer {
    private static TestServer testServer;
    private final String currencyPair = "BTC-USD";

    @BeforeAll
    static void setUp() {
        testServer = new TestServer(8887);
        testServer.start();
    }

    @AfterEach
    public void tearDown() {
        testServer.clearMessageQueue();
    }

    @Test
    public void testSimpleL2Update() throws URISyntaxException, InterruptedException {
        List<String> messages = new ArrayList<>();
        messages.add("json/snapshot-simple.json");
        messages.add("json/l2-update.json");
        testServer.loadMessages(messages);

        Client client = createClientAndConnectToTestServer();
        OrderBook orderBook = client.getOrderBook(currencyPair);
        Map<Double, Double> bids = orderBook.getBids();
        Map<Double, Double> asks = orderBook.getAsks();
        assertEquals(2, bids.size());
        assertEquals(1, asks.size());
    }

    @Test
    public void testCompoundL2Update() throws URISyntaxException, InterruptedException {
        List<String> messages = new ArrayList<>();
        messages.add("json/snapshot-simple.json");
        messages.add("json/l2-update-multiple.json");
        messages.add("json/l2-update-multiple-add.json");
        testServer.loadMessages(messages);

        Client client = createClientAndConnectToTestServer();
        OrderBook orderBook = client.getOrderBook(currencyPair);
        Map<Double, Double> bids = orderBook.getBids();
        Map<Double, Double> asks = orderBook.getAsks();
        assertEquals(3, bids.size());
        assertEquals(3, asks.size());
        assertEquals((Double) 5.0, bids.get(1.0));
    }

    @Test
    public void testSimpleL2ZeroUpdate() throws URISyntaxException, InterruptedException {
        List<String> messages = new ArrayList<>();
        messages.add("json/snapshot-simple.json");
        messages.add("json/l2-update.json");
        messages.add("json/l2-update-with-zero.json");
        testServer.loadMessages(messages);

        Client client = createClientAndConnectToTestServer();
        OrderBook orderBook = client.getOrderBook(currencyPair);
        Map<Double, Double> bids = orderBook.getBids();
        Map<Double, Double> asks = orderBook.getAsks();
        assertEquals(1, bids.size());
        assertEquals(1, asks.size());
    }

    @Test
    public void testMultipleL2ZeroUpdate() throws URISyntaxException, InterruptedException {
        List<String> messages = new ArrayList<>();
        messages.add("json/snapshot-simple.json");
        messages.add("json/l2-update-multiple.json");
        messages.add("json/l2-update-multiple-with-zeros.json");
        testServer.loadMessages(messages);

        Client client = createClientAndConnectToTestServer();
        OrderBook orderBook = client.getOrderBook(currencyPair);
        Map<Double, Double> bids = orderBook.getBids();
        Map<Double, Double> asks = orderBook.getAsks();
        assertEquals(2, bids.size());
        assertEquals(2, asks.size());
    }

    @Test
    public void testLargeSnapshotString() throws URISyntaxException, InterruptedException, IOException {
        List<String> messages = new ArrayList<>();
        messages.add("json/snapshot-large.json");
        testServer.loadMessages(messages);
        Client client = createClientAndConnectToTestServer();
        OrderBook orderBook = client.getOrderBook(currencyPair);
        String table = orderBook.getTableOutput(10);
        String fileTable = TestUtils.readFileFromPath("orderbook.txt");
        String stripWhiteSpace = Arrays.stream(table.split("\n")).map(String::trim).collect(Collectors.joining("\r\n"));
        assertEquals(fileTable, stripWhiteSpace);
    }

    private Client createClientAndConnectToTestServer() throws InterruptedException, URISyntaxException {
        OrderBook orderBook = new OrderBook(currencyPair);
        Client client = new Client(new URI("ws://localhost:8887"), List.of(currencyPair), 10);
        client.connectBlocking(10, TimeUnit.SECONDS);
        String jsonString = MessageBuildUtils.createSubscribeMessage(List.of(currencyPair), List.of("level2"));
        client.send(jsonString);
        Thread.sleep(500);
        return client;
    }
}

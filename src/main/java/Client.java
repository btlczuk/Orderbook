import messages.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;

import static utils.CommandLineUtils.clearConsole;

public class Client extends WebSocketClient {
    private final Gson gson;
    // Map[product_id -> OrderBook]
    private final Map<String, OrderBook> orderBooks = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private final List<String> subscriptions = new ArrayList<>();
    private final int printLevels;

    Client(URI uri, List<String> currencyPairs, int printLevels) {
        super(uri);
        currencyPairs.forEach(pair -> orderBooks.put(pair, new OrderBook(pair)));
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(Msg.class, new MessageDeserializer());
        gson = gb.create();
        this.printLevels = printLevels;
    }

    public List<String> getSubscriptions() {
        return new ArrayList<>(subscriptions);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("Server handshake");
    }

    @Override
    public void onMessage(String s) {
        log.debug("Received message " + s);
        Msg msg = gson.fromJson(s, Msg.class);
        if (msg instanceof SnapshotMsg) {
            handleMessage((SnapshotMsg) msg);
        } else if (msg instanceof L2UpdateMsg) {
            handleMessage((L2UpdateMsg) msg);
        } else if (msg instanceof SubscriptionsMsg) {
            handleMessage((SubscriptionsMsg) msg);
        } else if (msg instanceof ErrorMsg) {
            handleMessage((ErrorMsg) msg);
        } else {
            this.close();
            throw new IllegalArgumentException("No handler for this message type");
        }
    }

    public OrderBook getOrderBook(String currencyPair) {
        return new OrderBook(orderBooks.get(currencyPair));
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info("Close");
    }

    @Override
    public void onError(Exception e) {
        log.error("Error: " + e);
        this.close();
    }

    // This is the initial snapshot of the order book from
    // which we can construct our initial view
    // Assumes that the list of bids and asks coming in have distinct keys
    // s.t. no processing is required
    private void handleMessage(SnapshotMsg snapshotMsg) {
        log.info("Received snapshot message");
        orderBooks.get(snapshotMsg.product_id).consumeSnapshot(snapshotMsg);
    }

    private void handleMessage(ErrorMsg errorMsg) {
        throw new RuntimeException(errorMsg.message + " because " + errorMsg.reason);
    }

    private void handleMessage(L2UpdateMsg l2UpdateMsg) {
        log.debug("Received l2 update message");
        for (LinkedList<String> change : l2UpdateMsg.changes) {
            // Expected format is [side, price, size]
            if (change.size() == 3) {
                String type = change.removeFirst();
                switch (type) {
                    case "buy":
                        orderBooks.get(l2UpdateMsg.product_id).addBid(change);
                        break;
                    case "sell":
                        orderBooks.get(l2UpdateMsg.product_id).addAsk(change);
                        break;
                    default:
                }
            } else {
                log.warn("Unknown change format: " + change);
            }
        }
        printOrderBooks();
    }

    private void handleMessage(SubscriptionsMsg subscriptionsMsg) {
        log.info("Received subscriptions message");
        subscriptionsMsg.channels.forEach(channel -> subscriptions.addAll(channel.product_ids));
    }

    /**
     * Prints the tabular representation of
     * the initialised order books to the console
     */
    private void printOrderBooks() {
        clearConsole(log);
        for (OrderBook orderBook : orderBooks.values()) {
            if (orderBook.isInitialised()) System.out.println(orderBook.getTableOutput(this.printLevels));
        }
    }
}

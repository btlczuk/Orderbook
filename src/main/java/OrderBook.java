import messages.SnapshotMsg;
import utils.FormatUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class OrderBook {
    private final String currencyPair;
    private final Map<Double, Double> bids;
    private final Map<Double, Double> asks;
    private boolean initialised = false;

    public OrderBook(String currencyPair) {
        this.currencyPair = currencyPair;
        this.bids = new TreeMap<>(Collections.reverseOrder());
        this.asks = new TreeMap<>();
    }

    public OrderBook(OrderBook toClone) {
        this.initialised = toClone.initialised;
        this.currencyPair = toClone.currencyPair;
        this.bids = new TreeMap<>(Collections.reverseOrder());
        this.bids.putAll(toClone.bids);
        this.asks = new TreeMap<>();
        this.asks.putAll(toClone.asks);
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void addBid(List<String> bid) {
        updateMap(bid, bids);
    }

    public void addAsk(List<String> ask) {
        updateMap(ask, asks);
    }


    /**
     * Updates the given map to reflect the given change from an update message
     *
     * @param change Tuple of [price, size]
     * @param map order map to update
     */
    private void updateMap(List<String> change, Map<Double, Double> map) {
        Double price = Double.parseDouble(change.get(0));
        Double size = Double.parseDouble(change.get(1));
        // Match
        if (size == 0) {
            map.remove(price);
        } else {
            map.put(price, map.getOrDefault(price, 0.0) + size);
        }
    }

    public Map<Double, Double> getBids() {
        Map<Double, Double> bidMap = new TreeMap<>(Collections.reverseOrder());
        bidMap.putAll(bids);
        return bidMap;
    }

    public Map<Double, Double> getAsks() {
        return new TreeMap<>(asks);
    }

    /**
     * Batch method for adding bids
     *
     * @param bids from change field of update message
     */
    public void addBids(List<List<String>> bids) {
        for (List<String> bid : bids) {
            addBid(bid);
        }
    }

    /**
     * Batch method for adding asks
     *
     * @param asks from change field of update message
     */
    public void addAsks(List<List<String>> asks) {
        for (List<String> ask : asks) {
            addAsk(ask);
        }
    }

    /**
     * Consumes a snapshot message and uses this to initialise
     * the order book, populating both sides' maps.
     *
     * @param snapshotMsg from subscription
     */
    public void consumeSnapshot(SnapshotMsg snapshotMsg) {
        this.initialised = true;
        addBids(snapshotMsg.bids);
        addAsks(snapshotMsg.asks);
    }

    private String getLimitedListing(Map<Double, Double> map, int limit) {
        return map.entrySet().stream()
                .limit(limit)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    public String toString(int limit) {
        return "Bids: " +
                getLimitedListing(bids, limit) +
                "\n" +
                "Asks: " +
                getLimitedListing(asks, limit);
    }

    /**
     * Gets a tabular representation of the order book.
     * Asks first (descending order), bids second (ascending order).
     * Spread inbetween.
     *
     * @param limit the number of rows to return on each side of the book
     * @return a tabular representation of the order book
     */
    public String getTableOutput(int limit) {
        String c1 = currencyPair.substring(0, currencyPair.lastIndexOf('-'));
        String c2 = currencyPair.substring(currencyPair.lastIndexOf('-') + 1);
        List<String> headers = List.of(String.format("Size (%s)", c1), String.format("Price (%s)", c2));
        List<List<String>> outputTable = FormatUtils.getTableWithHeaderRow(headers);

        List<Map.Entry<Double,Double>> topAsks = topOrders(this.asks, limit);
        List<Map.Entry<Double,Double>> topBids = topOrders(this.bids, limit);

        double spread = topAsks.get(0).getKey() - topBids.get(0).getKey();

        List<List<String>> asks = getRows(topAsks);
        List<List<String>> bids = getRows(topBids);

        for (int i = asks.size() - 1; i >= 0; i--) {
            outputTable.add(asks.get(i));
        }

        List<String> spreadRow = List.of(String.format("Spread (%s)", c2), "" + spread);
        outputTable.add(spreadRow);

        outputTable.addAll(bids);

        return FormatUtils.formatAsTable(outputTable);
    }

    private List<Map.Entry<Double, Double>> topOrders(Map<Double, Double> map, int limit) {
        return map.entrySet().stream()
                .limit(limit).collect(Collectors.toList());
    }

    private List<List<String>> getRows(List<Map.Entry<Double, Double>> orders) {
        return orders.stream()
                .map(entry -> List.of("" + entry.getValue(), "" + entry.getKey()))
                .collect(Collectors.toList());
    }
}

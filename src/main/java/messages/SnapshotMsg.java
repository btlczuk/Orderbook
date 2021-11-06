package messages;

import java.util.List;

public class SnapshotMsg extends ResponseMsg {
    public final List<List<String>> bids;
    public final List<List<String>> asks;

    public SnapshotMsg(String type, String product_id, List<List<String>> bids, List<List<String>> asks) {
        super(type, product_id);
        this.bids = bids;
        this.asks = asks;
    }
}

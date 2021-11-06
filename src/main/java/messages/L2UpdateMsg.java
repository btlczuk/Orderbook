package messages;

import java.util.LinkedList;
import java.util.List;

public class L2UpdateMsg extends ResponseMsg {
    public final String time;
    public final List<LinkedList<String>> changes;

    public L2UpdateMsg(String type, String product_id, String time, List<LinkedList<String>> changes) {
        super(type, product_id);
        this.time = time;
        this.changes = changes;
    }
}

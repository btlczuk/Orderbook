package messages;

import java.util.List;

public class SubscribeMsg extends Msg {
    public final List<String> product_ids;
    public final List<String> channels;

    public SubscribeMsg(List<String> product_ids, List<String> channels) {
        super("subscribe");
        this.product_ids = product_ids;
        this.channels = channels;
    }
}

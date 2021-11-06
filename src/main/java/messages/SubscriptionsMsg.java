package messages;

import java.util.List;

public class SubscriptionsMsg extends Msg {
    public final List<Channel> channels;

    public SubscriptionsMsg(String type, List<Channel> channels) {
        super(type);
        this.channels = channels;
    }
}

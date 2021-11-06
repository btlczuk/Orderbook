package messages;

import java.util.List;

public class Channel {
    public final String name;
    public final List<String> product_ids;

    public Channel(String name, List<String> product_ids) {
        this.name = name;
        this.product_ids = product_ids;
    }
}

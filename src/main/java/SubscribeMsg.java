public class SubscribeMsg {
    private static final String type = "subscribe";
    private String[] product_ids;
    private String[] channels;

    public SubscribeMsg(String[] product_ids, String[] channels) {
        this.product_ids = product_ids;
        this.channels = channels;
    }
}

package messages;

public abstract class ResponseMsg extends Msg {

    public final String product_id;

    public ResponseMsg(String type, String product_id) {
        super(type);
        this.product_id = product_id;
    }
}

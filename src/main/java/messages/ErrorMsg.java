package messages;

public class ErrorMsg extends Msg {
    public final String message;
    public final String reason;

    public ErrorMsg(String type, String message, String reason) {
        super(type);
        this.message = message;
        this.reason = reason;
    }
}

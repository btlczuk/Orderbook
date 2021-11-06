package messages;

public abstract class Msg {
    public final String type;

    public Msg(String type) {
        this.type = type;
    }
}

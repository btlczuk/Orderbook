import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        if (args.length < 1) {
            throw new IllegalStateException("Argument list should not be empty. Expected instrument to subscribe to.");
        }
        Client client = new Client(new URI("wss://ws-feed.exchange.coinbase.com"));
        client.connectBlocking(10, TimeUnit.SECONDS);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
        Gson gson = gsonBuilder.create();
        String[] channels = new String[]{"level2"};
        SubscribeMsg subscribeMsg = new SubscribeMsg(args, channels);
        String jsonString = gson.toJson(subscribeMsg);
        client.send(jsonString);
    }
}
package messages;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class MessageDeserializer implements JsonDeserializer<Msg> {
    public Msg deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if (json == null)
            return null;
        else {
            String type = json.getAsJsonObject().get("type").getAsString();
            switch(type){
                case "snapshot":
                    return context.deserialize(json, SnapshotMsg.class);
                case "l2update":
                    return context.deserialize(json, L2UpdateMsg.class);
                case "subscriptions":
                    return context.deserialize(json, SubscriptionsMsg.class);
                case "subscribe":
                    return context.deserialize(json, SubscribeMsg.class);
                case "error":
                    return context.deserialize(json, ErrorMsg.class);
                default:
                    return null;
            }
        }

    }
}

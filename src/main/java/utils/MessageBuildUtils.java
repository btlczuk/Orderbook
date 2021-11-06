package utils;

import messages.SubscribeMsg;
import com.google.gson.Gson;

import java.util.List;

public class MessageBuildUtils {
    public static String createSubscribeMessage(List<String> products_ids, List<String> channels) {
        Gson gson = new Gson();
        SubscribeMsg subscribeMsg = new SubscribeMsg(products_ids, channels);
        return gson.toJson(subscribeMsg);
    }
}

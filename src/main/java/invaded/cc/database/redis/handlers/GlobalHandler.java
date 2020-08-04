package invaded.cc.database.redis.handlers;

import com.google.gson.JsonObject;
import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.JedisHandler;
import invaded.cc.database.redis.reader.JedisActionReader;

public class GlobalHandler implements JedisHandler {
    @Override
    public void handle(String channel, JsonObject jsonObject) {
        String action = jsonObject.get("action").getAsString();

        JedisAction jedisAction = JedisAction.valueOf(action);
        JedisActionReader.readers.get(jedisAction).getCallback().callback(jsonObject);
    }
}

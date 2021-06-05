package invaded.cc.core.database.redis.handlers;

import invaded.cc.core.database.redis.JedisAction;
import invaded.cc.core.database.redis.JedisHandler;
import invaded.cc.core.database.redis.reader.JedisActionReader;
import net.minecraft.util.com.google.gson.JsonObject;

public class GlobalHandler implements JedisHandler {
    @Override
    public void handle(String channel, JsonObject jsonObject) {
        String action = jsonObject.get("action").getAsString();

        JedisAction jedisAction = JedisAction.valueOf(action);
        JedisActionReader.readers.get(jedisAction).getCallback().callback(jsonObject);
    }
}

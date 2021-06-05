package invaded.cc.core.database.redis;


import net.minecraft.util.com.google.gson.JsonObject;

public interface JedisHandler {

    void handle(String channel, JsonObject jsonObject);

}

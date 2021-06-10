package invaded.cc.core.util.jedis;


import net.minecraft.util.com.google.gson.JsonObject;

public interface JedisHandler {

    void handle(String channel, JsonObject jsonObject);

}

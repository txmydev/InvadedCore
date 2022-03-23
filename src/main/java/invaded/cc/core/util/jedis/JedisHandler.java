package invaded.cc.core.util.jedis;


import invaded.cc.common.library.gson.JsonObject;

public interface JedisHandler {

    void handle(String channel, JsonObject jsonObject);

}

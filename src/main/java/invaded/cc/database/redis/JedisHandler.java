package invaded.cc.database.redis;


import com.google.gson.JsonObject;

public interface JedisHandler {

    void handle(String channel, JsonObject jsonObject);

}

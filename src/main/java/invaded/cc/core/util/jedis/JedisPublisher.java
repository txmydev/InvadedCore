package invaded.cc.core.util.jedis;

import invaded.cc.core.Spotify;
import net.minecraft.util.com.google.gson.JsonObject;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisPublisher {

    private String channel;
    private JedisConfiguration conf;
    private JedisPool pool;

    public JedisPublisher(JedisConfiguration conf, String channel) {
        this.channel = channel;
        this.pool = Spotify.getInstance().getRedisDatabase().getJedisPool();

        this.conf = conf;
    }

    public void write(JsonObject jsonObject) {
        try {
            pool.getResource().publish(channel, jsonObject.toString());
        } catch (JedisConnectionException ex) {
            ex.printStackTrace();
        }
    }

}

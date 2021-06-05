package invaded.cc.core.database.redis;

import lombok.Setter;
import net.minecraft.util.com.google.gson.JsonObject;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisPublisher {

    @Setter
    private static JedisPool pool;

    private String channel;
    private JedisConfiguration conf;

    public JedisPublisher(JedisConfiguration conf, String channel){
        this.channel = channel;

        this.conf = conf;
    }

    public void write(JsonObject jsonObject){
        try {
           pool.getResource().publish(channel, jsonObject.toString());
        }catch(JedisConnectionException ex){
            ex.printStackTrace();
        }
    }


    public void close() {
        pool.close();
    }
}

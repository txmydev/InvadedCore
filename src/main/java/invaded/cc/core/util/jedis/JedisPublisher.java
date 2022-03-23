package invaded.cc.core.util.jedis;

import invaded.cc.common.library.gson.JsonObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisPublisher {

    private String channel;
    private JedisConfiguration config;
    private Jedis jedis;
    private JedisPool pool;

    public JedisPublisher(JedisConfiguration config, String channel) {
        this.channel = channel;
        this.config = config;
        this.open();
    }

    public void write(JsonObject jsonObject) {
        try {
            jedis.publish(channel, jsonObject.toString());
        } catch (JedisConnectionException ex) {
            System.out.println("Got an err from redis, attempting to restablish connection: " + ex.getLocalizedMessage());

            close();
            open();
        }
    }

    private void open() {
        pool = new JedisPool(new JedisPoolConfig(), config.getHost(), config.getPort(), 15000, config.getPassword());;
        jedis = pool.getResource();
    }

    public void close() {
        if(jedis != null) jedis.close();
        if(pool != null) pool.close();

        jedis = null;
        pool = null;
    }
}

package invaded.cc.core.util.jedis;

import invaded.cc.common.library.gson.JsonObject;
import invaded.cc.common.library.gson.JsonParser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class JedisSubscriber {
    private JedisPool pool;

    private String channel;
    private JedisHandler handler;
    private JedisConfiguration config;
    private Jedis jedis;

    private JedisPubSub sub;

    public JedisSubscriber(JedisConfiguration config, String channel, final JedisHandler handler) {
        this.config = config;
        this.channel = channel;
        this.handler = handler;
        this.pool = new JedisPool(new JedisPoolConfig(), config.getHost(), config.getPort(), 15000, config.getPassword());
        this.jedis = pool.getResource();
        this.sub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
                handler.handle(channel, jsonObject);
            }
        };

        new Thread(() -> {
            try {

                jedis.subscribe(this.sub, channel);

                Thread.sleep(50L * 5L);
            } catch (Exception ex) {
                System.out.println("Jedis got broken mate, im trying to re open it...");

                stop();
                initPool();
            }
        }).start();
    }

    private void initPool() {
        this.pool = new JedisPool(new JedisPoolConfig(), config.getHost(), config.getPort(), 15000, config.getPassword());
        this.jedis = pool.getResource();
    }

    public void stop() {
        if (sub != null) sub.unsubscribe();
        if(jedis != null) jedis.close();

        pool.close();
    }
}

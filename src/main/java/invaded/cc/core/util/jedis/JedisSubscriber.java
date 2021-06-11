package invaded.cc.core.util.jedis;

import invaded.cc.core.Spotify;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class JedisSubscriber {

    private JedisPool pool;

    private String channel;
    private JedisHandler handler;
    private JedisConfiguration conf;
    private Thread thread;

    private JedisPubSub sub;

    public JedisSubscriber(JedisConfiguration conf, String channel, final JedisHandler handler) {
        this.conf = conf;
        this.channel = channel;
        this.handler = handler;
        this.pool = Spotify.getInstance().getRedisDatabase().getJedisPool();

        this.sub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
                handler.handle(channel, jsonObject);
            }
        };

        Jedis jedis = pool.getResource();

        thread = new Thread(() -> {
            while(true) {
                System.out.println("subscribing to " + channel);
                try {
                    if (jedis != null) jedis.subscribe(sub, channel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void stop() {
        if (thread.isAlive()) thread.stop();
        if (sub != null) sub.unsubscribe();
    }

}

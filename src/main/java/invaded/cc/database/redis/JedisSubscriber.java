package invaded.cc.database.redis;

import lombok.Setter;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class JedisSubscriber {

    @Setter
    private static JedisPool pool;

    private String channel;
    private JedisHandler handler;
    private JedisConfiguration conf;

    private JedisPubSub sub;

    public JedisSubscriber(JedisConfiguration conf, String channel, final JedisHandler handler){
        this.conf = conf;
        this.channel = channel;
        this.handler = handler;

        this.sub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
                handler.handle(channel, jsonObject);
            }
        };

        new Thread(() -> {
            try{
                pool.getResource().subscribe(this.sub, channel);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        if(sub != null) sub.unsubscribe();
        pool.close();
    }

}

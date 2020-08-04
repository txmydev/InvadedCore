package invaded.cc.database.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class JedisSubscriber {

    private String channel;
    private JedisHandler handler;
    private JedisConfiguration conf;

    private Jedis jedis;
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

        this.jedis = new Jedis(conf.getHost(), conf.getPort());

        if(conf.isAuth()) jedis.auth(conf.getPassword());

        new Thread(() -> {
            try{
                jedis.subscribe(this.sub, channel);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        if(sub != null) sub.unsubscribe();
        jedis.close();
    }

}

package invaded.cc.database.redis;

import com.google.gson.JsonObject;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisPublisher {

    private String channel;
    private JedisConfiguration conf;

    @Getter
    private Jedis jedis;

    private JsonObject lastMessage;

    public JedisPublisher(JedisConfiguration conf, String channel){
        this.channel = channel;

        this.jedis = new Jedis(conf.getHost(), conf.getPort());
        this.conf = conf;

        if(conf.isAuth())
            jedis.auth(conf.getPassword());
    }

    public void write(JsonObject jsonObject){
        if(channel.equalsIgnoreCase("invaded-channel")
        && isDuplicated(jsonObject)) {
            System.out.println("[Core] Attempted to publish 2 times in a row the same message.");
            return;
        }

        try {
            jedis.publish(channel, jsonObject.toString());
            lastMessage = jsonObject;

            if(channel.equals("invaded-channel")) {
                System.out.println("Published something in the channel.");
            }
        }catch(JedisConnectionException ex){
            jedis.close();
            jedis = new Jedis(conf.getHost(), conf.getPort());

            if(conf.isAuth()) jedis.auth(conf.getPassword());

            jedis.publish(channel, jsonObject.toString());
            lastMessage = jsonObject;

            if(channel.equals("invaded-channel"))
                System.out.println("Published something in the channel (error first).");
        }
    }

    public boolean isDuplicated(JsonObject jsonObject) {
        if(lastMessage == null) return false;
        return lastMessage.toString().equalsIgnoreCase(jsonObject.toString());
    }

}

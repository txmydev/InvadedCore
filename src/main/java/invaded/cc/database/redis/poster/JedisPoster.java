package invaded.cc.database.redis.poster;

import invaded.cc.Core;
import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.JedisConfiguration;
import invaded.cc.database.redis.JedisPublisher;
import invaded.cc.util.ConfigFile;
import invaded.cc.util.ConfigTracker;
import net.minecraft.util.com.google.gson.JsonObject;

public class JedisPoster {

    private static final JedisConfiguration CONF;
    private static JedisPublisher INVADED_CHANNEL_PUBLISHER;

    static {
        ConfigFile configFile = Core.getInstance().getDatabaseConfig();
        ConfigTracker configTracker = new ConfigTracker(configFile, "redis");

        CONF = new JedisConfiguration(configTracker.getString("host"),
                configTracker.getInt("port"),
                configTracker.getString("username"),
                configTracker.getString("password"),
                true);
    }


    private JsonObject jsonObject = new JsonObject();
    private String channel = "invaded-channel";

    public JedisPoster(JedisAction jedisAction) {
        jsonObject.addProperty("server-id", Core.getInstance().getServerName());
        jsonObject.addProperty("action", jedisAction.name());

        if(INVADED_CHANNEL_PUBLISHER == null) INVADED_CHANNEL_PUBLISHER = new JedisPublisher(CONF, channel);
    }

    public JedisPoster channel(String channel) {
        this.channel = channel;
        return this;
    }

    public JedisPoster addInfo(String id, Character val) {
        jsonObject.addProperty(id, val);
        return this;
    }

    public JedisPoster addInfo(String id, Number val){
        jsonObject.addProperty(id, val);
        return this;
    }

    public JedisPoster addInfo(String id, String val) {
        jsonObject.addProperty(id, val);
        return this;
    }

    public JedisPoster addInfo(String id, Boolean val) {
        jsonObject.addProperty(id, val);
        return this;
    }

    public void post() {
        JedisPublisher publisher = INVADED_CHANNEL_PUBLISHER;
        publisher.write(jsonObject);
    }


}

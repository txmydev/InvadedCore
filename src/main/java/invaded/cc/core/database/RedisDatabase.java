package invaded.cc.core.database;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.jedis.JedisConfiguration;
import invaded.cc.core.util.ConfigFile;
import invaded.cc.core.util.ConfigTracker;
import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Getter
public class RedisDatabase {

    private JedisConfiguration config;
    private JedisPool jedisPool;
    private boolean redisMode;

    public RedisDatabase() {
        ConfigFile configFile = Spotify.getInstance().getDatabaseConfig();
        ConfigTracker configTracker = new ConfigTracker(configFile, "redis");
        this.redisMode = configTracker.getBoolean("enabled");
        if(!redisMode) return;

        if (configTracker.getBoolean("authentication"))
            config = new JedisConfiguration(
                    configTracker.getString("host"),
                    configTracker.getInt("port"),
                    configTracker.getString("username"),
                    configTracker.getString("password"),
                    true
            );
        else
            config = new JedisConfiguration(configTracker.getString("host"),
                    configTracker.getInt("port"));

        this.jedisPool = new JedisPool(new JedisPoolConfig(), config.getHost(), config.getPort(), 4000, config.getPassword());
    }

    public void shutdown() {
        if(!this.jedisPool.isClosed()) this.jedisPool.close();
    }
}

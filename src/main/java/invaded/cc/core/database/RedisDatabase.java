package invaded.cc.core.database;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.jedis.JedisConfiguration;
import invaded.cc.core.util.ConfigFile;
import invaded.cc.core.util.ConfigTracker;
import lombok.Getter;
import redis.clients.jedis.Jedis;
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

    public <T> T executeCommand(RedisCommand<T> command) {
        Jedis resource = this.jedisPool.getResource();
        T result = command.execute(resource);
        resource.close();
        return result;
    }

    public void shutdown() {
        this.executeCommand((jedis) ->{
            jedis.hset("server-" + Spotify.SERVER_NAME, "lastRestart", System.currentTimeMillis() +"");
            System.out.println("saving lastRestart to " + jedis.hget("server-"+Spotify.SERVER_NAME, "lastRestart"));
            return null;
        });

        if(!this.jedisPool.isClosed()) this.jedisPool.close();
    }
}

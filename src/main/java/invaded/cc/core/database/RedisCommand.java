package invaded.cc.core.database;

import redis.clients.jedis.Jedis;

public interface RedisCommand<T> {

    T execute(Jedis jedis);

}

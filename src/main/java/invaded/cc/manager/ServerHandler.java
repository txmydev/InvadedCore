package invaded.cc.manager;

import invaded.cc.Core;
import invaded.cc.database.redis.JedisManager;
import invaded.cc.database.redis.JedisPublisher;
import invaded.cc.database.redis.JedisSubscriber;
import invaded.cc.database.redis.handlers.ServerSubscriptionHandler;
import invaded.cc.profile.User;
import invaded.cc.server.Server;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.com.google.gson.JsonObject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerHandler {

    @Getter
    private final ConcurrentMap<String, Server> servers;

    private final JedisSubscriber subscriber;
    private JedisPublisher publisher;

    @Setter
    @Getter
    private boolean maintenance = false, joineable = true, whitelist = false;

    @Setter
    @Getter
    private String motd = "";

    public ServerHandler() {
        JedisManager jedis = Core.getInstance().getDb().getRedisManager();
        this.servers = new ConcurrentHashMap<>();

        subscriber = new JedisSubscriber(jedis.getConfig(), "server-channel", new ServerSubscriptionHandler());
        publisher = new JedisPublisher(jedis.getConfig(), "server-channel");
    }

    public void close() {
        subscriber.stop();
    }

    public Server getServer(String serverId) {
        return servers.get(serverId);
    }

    public void sendData(JsonObject jsonObject) {
        publisher.write(jsonObject);
    }

    public Server getCurrentServer() {
        return servers.get(Core.getInstance().getServerName());
    }

    public User find(String name) {
        User globalPlayer = null;

        for (Server server : servers.values()) {
            for (User player : server.getPlayers()) {
                if (!player.getName().equalsIgnoreCase(name)) continue;

                globalPlayer = player;
            }
        }

        return globalPlayer;
    }

    public void removePlayer(User globalPlayer) {
        for (Server server : servers.values()) server.getPlayers().remove(globalPlayer);
    }
}

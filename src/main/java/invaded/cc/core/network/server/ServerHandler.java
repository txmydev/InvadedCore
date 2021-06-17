package invaded.cc.core.network.server;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.Task;
import invaded.cc.core.util.perms.PermLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter @Setter
public class ServerHandler {

    private ConcurrentMap<String, Server> serverMap;

    private boolean testing;
    private boolean maintenance;
    private String extraInfo = "";

    public ServerHandler() {
        this.serverMap = new ConcurrentHashMap<>();

        serverMap.computeIfAbsent(Spotify.SERVER_NAME, val -> {
            Server server = new Server(val);
            server.setLastUpdate(System.currentTimeMillis());
            return server;
        });

        new ServerHeartbeatTask().runTaskTimerAsynchronously(Spotify.getInstance(), 20L, 20L);
        new ServerTimeoutTask().runTaskTimerAsynchronously(Spotify.getInstance(), 60L, 20L);
    }

    public Server getServer(String name) {
        return serverMap.get(name);
    }

    public Server createServer(String name) {
        Server server = new Server(name);

        serverMap.computeIfAbsent(name, n -> {
            Common.broadcastMessage(PermLevel.ADMIN, "&7[&d&lServer Heartbeat&7] &eServer &f'" + name + "' &eis back &aonline&e.");
            return server;
        });

        server.setRecentlyCreated(true);

        return server;
    }

    public void setMaintenanceMode(String name, boolean value) {
        Server server = serverMap.get(name);
        if(name.equals(Spotify.SERVER_NAME)) this.maintenance = value;
        if(server != null) {
            server.setMaintenance(value);
            Common.broadcastMessage(PermLevel.ADMIN, "&7[&d&lServer Heartbeat&7] &eServer &f'" + name + (value ? "' &eis now in" : "&eis no longer in")
                    + " &bmaintenance mode&e.");
        }
    }

    public void setTestingMode(String name, boolean value) {
        Server server = serverMap.get(name);
        if(name.equals(Spotify.SERVER_NAME)) this.testing = value;

        if(server != null) {
            server.setTesting(value);
            Common.broadcastMessage(PermLevel.ADMIN, "&7[&d&lServer Heartbeat&7] &eServer &f'" + name + (value ? "' &eis now in" : "&eis no longer in")
                    + " &btesting mode&e.");
        }
    }
}

package invaded.cc.core.network.server;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.CC;
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
    private ConcurrentMap<String, Long> lastCreatedTime;

    private boolean testing;
    private boolean maintenance;
    private String extraInfo = "";

    private final ServerHeartbeatTask heartbeatTask;
    private final ServerTimeoutTask timeoutTask;

    private final String prefix = CC.D_GRAY + "[" + CC.YELLOW + "Server Monitor" + CC.D_GRAY + "] " + CC.RESET;

    public ServerHandler() {
        this.serverMap = new ConcurrentHashMap<>();
        this.lastCreatedTime = new ConcurrentHashMap<>();

        serverMap.computeIfAbsent(Spotify.SERVER_NAME, val -> {
            Server server = new Server(val);
            server.setLastUpdate(System.currentTimeMillis());
            return server;
        });

        heartbeatTask = new ServerHeartbeatTask(); heartbeatTask.runTaskTimerAsynchronously(Spotify.getInstance(), 20L, 20L);
        timeoutTask = new ServerTimeoutTask(); timeoutTask.runTaskTimerAsynchronously(Spotify.getInstance(), 60L, 5L * 20L);
    }

    public void shutdown() {
        heartbeatTask.cancel();
        timeoutTask.cancel();
    }

    public Server getServer(String name) {
        return serverMap.get(name);
    }

    public Server createServer(String name) {
        Server server = new Server(name);

        serverMap.computeIfAbsent(name, n -> {
            Common.broadcastIf(PermLevel.ADMIN, prefix + CC.YELLOW + name + CC.RESET + " is back " + CC.GREEN + "online" + CC.WHITE + ".", Profile::isStaffAlerts);

            // if((System.currentTimeMillis() - lastCreatedTime.computeIfAbsent(name, str -> System.currentTimeMillis()) > 8000)) Common.broadcastMessage(PermLevel.ADMIN, "&7[&d&lServer Heartbeat&7] &eServer &f'" + name + "' &eis back &aonline&e.");
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
            Common.broadcastIf(PermLevel.ADMIN, prefix + CC.YELLOW + name + " &fis " + (value ? "&anow" : "&cno longer") + " &fin maintenance.", Profile::isStaffAlerts
            );
        }
    }

    public void setTestingMode(String name, boolean value) {
        Server server = serverMap.get(name);
        if(name.equals(Spotify.SERVER_NAME)) this.testing = value;

        if(server != null) {
            server.setTesting(value);
            Common.broadcastIf(PermLevel.ADMIN, prefix + CC.YELLOW + name + " &fis " + (value ? "&anow" : "&cno longer") + " &ftesting.", Profile::isStaffAlerts);
        }
    }
}

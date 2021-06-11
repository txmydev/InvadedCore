package invaded.cc.core.network.server;

import invaded.cc.core.util.Common;
import invaded.cc.core.util.perms.PermLevel;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ServerHandler {

    private Map<String, Server> serverMap;

    public ServerHandler() {
        this.serverMap = new HashMap<>();

        ServerHeartbeatTask.start();
    }

    public Server getServer(String name) {
        return serverMap.get(name);
    }

    public Server createServer(String name) {
        Server server = new Server(name);
        serverMap.put(name, server);

        Common.broadcastMessage(PermLevel.ADMIN, "&7[&d&lServer Heartbeat&7] &eServer &f'" + name + "' &eis back &aonline&e.");

        return server;
    }

    public void deleteServer(String name) {
        if(!serverMap.containsKey(name)) return;

        serverMap.remove(name);
        Common.broadcastMessage(PermLevel.ADMIN, "&7[&d&lServer Heartbeat&7] &eServer &f'" + name + "' &eis now &coffline&e.");
    }

    public void setMaintenanceMode(String name, boolean value) {
        if(!serverMap.containsKey(name)) return;
        Server server = serverMap.get(name);

        server.setMaintenance(value);
        Common.broadcastMessage(PermLevel.ADMIN, "&7[&d&lServer Heartbeat&7] &eServer &f'" + name + (value ? "' &eis now in" : "&eis no longer in")
         + " &bmaintenance mode&e.");
    }

    public void setTestingMode(String name, boolean value) {
        if(!serverMap.containsKey(name)) return;
        Server server = serverMap.get(name);

        server.setTesting(value);
        Common.broadcastMessage(PermLevel.ADMIN, "&7[&d&lServer Heartbeat&7] &eServer &f'" + name + (value ? "' &eis now in" : "&eis no longer in")
                + " &btesting mode&e.");
    }
}

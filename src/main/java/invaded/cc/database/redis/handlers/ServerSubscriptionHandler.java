package invaded.cc.database.redis.handlers;

import com.google.gson.JsonObject;
import invaded.cc.Core;
import invaded.cc.manager.ServerHandler;
import invaded.cc.server.Server;
import invaded.cc.database.redis.JedisHandler;
import invaded.cc.util.Common;
import invaded.cc.util.perms.PermLevel;

import java.util.Iterator;

public class ServerSubscriptionHandler implements JedisHandler {

    @Override
    public void handle(String channel, JsonObject jsonObject) {
        if (!channel.equalsIgnoreCase("server-channel")) return;

        String serverId = jsonObject.get("server-id").getAsString();
        int onlinePlayers = jsonObject.get("onlinePlayers").getAsInt();
        int maxPlayers = jsonObject.get("maxPlayers").getAsInt();
        boolean whitelist = jsonObject.get("whitelist").getAsBoolean();
        boolean maintenance = jsonObject.get("maintenance").getAsBoolean();
        boolean joineable = jsonObject.get("joineable").getAsBoolean();
        String motd = jsonObject.get("motd").getAsString();

        ServerHandler serverHandler = Core.getInstance().getServerHandler();
        Server server = serverHandler.getServer(serverId);

        if (server == null) {
            Core.getInstance().getServerHandler().getServers().put(serverId, new Server(serverId));
            server = Core.getInstance().getServerHandler().getServer(serverId);

            Common.broadcastMessage(PermLevel.ADMIN, "&7[&bServer Heartbeat&7] &fAdded server &b" +serverId +"&f...");
        }

        server.setOnlinePlayers(onlinePlayers);
        server.setMaxPlayers(maxPlayers);
        server.setMaintenance(maintenance);
        server.setJoineable(joineable);
        server.setWhitelist(whitelist);
        server.setLastUpdate(System.currentTimeMillis());
        server.setMotd(motd);

        Iterator<Server> iterator = serverHandler.getServers().values().iterator();

        for (; iterator.hasNext(); ) {
            Server target = iterator.next();

            if(System.currentTimeMillis() - target.getLastUpdate() >= 3000L) {
                Common.broadcastMessage(PermLevel.ADMIN, "&7[&bServer Heartbeat&7] &fRemoved server &b" + target.getServerId() +"&f...");
                iterator.remove();
            }
        }
    }

}

package invaded.cc.core.database.redis.handlers;

import invaded.cc.core.database.redis.JedisHandler;
import invaded.cc.core.manager.ServerHandler;
import invaded.cc.core.server.Server;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.perms.PermLevel;
import net.minecraft.util.com.google.gson.JsonObject;

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

        ServerHandler serverHandler = null;// Core.getInstance().getServerHandler();
        if (!serverHandler.getServers().containsKey(serverId))
            Common.broadcastMessage(PermLevel.ADMIN, "&7[&bServer Heartbeat&7] &fAdded server &b" + serverId + "&f...");

        serverHandler.getServers().putIfAbsent(serverId, new Server(serverId));
        Server server = serverHandler.getServer(serverId);

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

            if (System.currentTimeMillis() - target.getLastUpdate() >= 3000L) {
                Common.broadcastMessage(PermLevel.ADMIN, "&7[&bServer Heartbeat&7] &fRemoved server &b" + target.getServerId() + "&f...");
                iterator.remove();
            }
        }
    }

}

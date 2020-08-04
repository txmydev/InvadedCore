package invaded.cc.tasks;

import invaded.cc.Core;
import invaded.cc.manager.ServerHandler;
import invaded.cc.util.Common;
import invaded.cc.util.json.JsonChain;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerUpdateTask extends BukkitRunnable {

    public ServerUpdateTask(){
        runTaskTimerAsynchronously(Core.getInstance(), 1L, 3L);
    }

    @Override
    public void run() {
        JsonChain jsonChain = new JsonChain();
        ServerHandler serverHandler = Core.getInstance().getServerHandler();

        jsonChain.addProperty("server-id", Core.getInstance().getServerName())
                .addProperty("onlinePlayers", Common.getOnlinePlayers().size())
                .addProperty("maxPlayers", Bukkit.getMaxPlayers())
                .addProperty("whitelist", serverHandler.isWhitelist())
                .addProperty("maintenance", serverHandler.isMaintenance())
                .addProperty("joineable", serverHandler.isJoineable())
                .addProperty("motd", serverHandler.getMotd());

        serverHandler.sendData(jsonChain.get());
    }
}

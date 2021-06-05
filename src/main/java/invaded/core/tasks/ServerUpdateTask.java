package invaded.core.tasks;

import invaded.core.Spotify;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerUpdateTask extends BukkitRunnable {

    public ServerUpdateTask(){
        runTaskTimerAsynchronously(Spotify.getInstance(), 1L, 3L);
    }

    @Override
    public void run() {
     /*   JsonChain jsonChain = new JsonChain();
        ServerHandler serverHandler = Core.getInstance().getServerHandler();

        jsonChain.addProperty("server-id", Core.getInstance().getServerName())
                .addProperty("onlinePlayers", Common.getOnlinePlayers().size())
                .addProperty("whitelist", serverHandler.isWhitelist())
                .addProperty("maintenance", serverHandler.isMaintenance())
                .addProperty("joineable", serverHandler.isJoineable())
                .addProperty("motd", serverHandler.getMotd());

        serverHandler.sendData(jsonChain.get());*/
    }
}

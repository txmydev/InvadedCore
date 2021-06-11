package invaded.cc.core.network.server;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class ServerTimeoutTask extends BukkitRunnable {
    @Override
    public void run() {
        Iterator<Server> iterator = Spotify.getInstance().getServerHandler().getServerMap().values().iterator();

        while (iterator.hasNext()) {
            Server target = iterator.next();
            if(target.isRecentlyCreated()) continue;
            if (System.currentTimeMillis() - target.getLastUpdate() >= 5000L) {
                Common.broadcastMessage(PermLevel.ADMIN, "&7[&d&lServer Heartbeat&7] &eServer &f'" + target.getName() + "' &eis now &coffline&e.");
                iterator.remove();
            }
        }
    }
}

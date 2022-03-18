package invaded.cc.core.network.server;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class ServerTimeoutTask extends BukkitRunnable {

    private final Spotify plugin = Spotify.getInstance();

    @Override
    public void run() {

        Iterator<Server> iterator = plugin.getServerHandler().getServerMap().values().iterator();

        while (iterator.hasNext()) {
            Server target = iterator.next();
            if(target.isRecentlyCreated()) continue;
            long now = System.currentTimeMillis();
            if (now - target.getLastUpdate() >= 5000L) {
                Common.broadcastIf(PermLevel.ADMIN,  plugin.getServerHandler().getPrefix() + target.getName() + CC.YELLOW + " is now " + CC.RED + "offline" + CC.YELLOW + ".", Profile::isStaffAlerts);

                //Common.broadcastMessage(PermLevel.ADMIN, "&7[&d&lServer Heartbeat&7] &eServer &f'" + target.getName() + "' &eis now &coffline&e.");
                iterator.remove();
            }
        }
    }
}

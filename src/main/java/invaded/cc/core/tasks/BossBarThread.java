package invaded.cc.core.tasks;

import invaded.cc.core.Spotify;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BossBarThread extends BukkitRunnable {
    @Override
    public void run() {
        if(Spotify.getInstance().getBossbarHandler().getAdapter() != null)
            Bukkit.getOnlinePlayers().forEach(player -> {
                if(player.getTicksLived() <= 20 * 3) return;

                Spotify.getInstance().getBossbarHandler().display(player);
            });
    }
}

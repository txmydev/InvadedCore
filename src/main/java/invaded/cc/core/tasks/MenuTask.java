package invaded.cc.core.tasks;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.menu.Menu;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuTask extends BukkitRunnable {

    public MenuTask() {
        runTaskTimerAsynchronously(Spotify.getInstance(), 20L, 5L);
    }

    @Override
    public void run() {
        try {
            for (Menu menu : Menu.menus) {
                if (menu.isUpdate()) menu.update();
            }
        } catch (Exception ignored) {
        }
    }
}

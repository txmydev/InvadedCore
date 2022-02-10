package invaded.cc.core.tasks;

import invaded.cc.core.Spotify;
import invaded.cc.core.bossbar.BossbarHandler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BossBarThread extends Thread {

    public BossBarThread() {
        super("Spotify - Bossbar thread");
        setDaemon(false);
    }

    @Override
    public void run() {
        while(true) {
            BossbarHandler handler = Spotify.getInstance().getBossbarHandler();
            if(handler.getAdapter() != null){
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if(player.getTicksLived() <= 20 * 3) return;
                    handler.display(player);
                });

                try {
                    Thread.sleep(50L * handler.getAdapter().getInterval());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}

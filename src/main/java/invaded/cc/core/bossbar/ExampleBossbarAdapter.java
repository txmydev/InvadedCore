package invaded.cc.core.bossbar;

import invaded.cc.core.Spotify;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ExampleBossbarAdapter implements BossbarAdapter{

    private final List<Player> ignoredPlayers = Collections.EMPTY_LIST;
    private final BukkitTask task;
    private int timer = 60;
    private int maxTimer = timer;
    private String title;

    public ExampleBossbarAdapter() {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(Spotify.getInstance(),
                this::tick, 20L, 20L);
    }

    public void tick() {
        timer--;
        title = "Timer: " + timer;

        if(timer == 0) {
            timer = this.maxTimer;
        }
    }


    @Override
    public long getInterval() {
        return 20L;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public double getHealth() {
        return (this.timer * 200.D) / this.maxTimer;
    }

    @Override
    public List<Player> getIgnoredPlayers() {
        return this.ignoredPlayers;
    }
}

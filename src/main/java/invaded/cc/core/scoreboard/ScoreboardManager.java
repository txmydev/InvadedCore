package invaded.cc.core.scoreboard;

import invaded.cc.core.Spotify;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ScoreboardManager extends Thread implements Listener {

    @Getter private invaded.cc.core.scoreboard.ScoreboardProvider provider;

    private final Map<UUID, PlayerScoreboard> scoreboards = new ConcurrentHashMap<>();

    public ScoreboardManager(Spotify plugin) {
        super("Spotify - Scoreboard Update Thread");

        setDaemon(true);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        start();
    }

    @Override
    public void run() {
        while (true) {
            try {

                scoreboards.forEach((uniqueId, scoreboard) -> {
                    if (provider != null)
                        scoreboard.update();
                });

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            }
    }

    public void setProvider(ScoreboardProvider provider) {
        this.provider = provider;

        if(Bukkit.getOnlinePlayers().size() > 0) {
            scoreboards.clear();
            Bukkit.getOnlinePlayers().forEach(player -> scoreboards.putIfAbsent(player.getUniqueId(), new PlayerScoreboard(provider, player)));
        }
    }

    public invaded.cc.core.scoreboard.PlayerScoreboard getScoreboard(Player player) {
        return scoreboards.get(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        scoreboards.put(player.getUniqueId(), new PlayerScoreboard(provider, player));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerScoreboard scoreboard = getScoreboard(player);
        if (scoreboard != null) {
            scoreboard.unregister();
            scoreboards.remove(player.getUniqueId());
        }
    }
}
package invaded.cc.core.tablist;

import com.google.common.collect.Maps;
import invaded.cc.core.Spotify;
import invaded.cc.core.util.Common;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

@Getter
public class TablistHandler {

    private final Spotify plugin;

    @Setter
    private TabAdapter adapter;
    private Map<UUID, Tablist> playerTabs = Maps.newHashMap();
    private TablistThread thread;

    public TablistHandler(Spotify plugin) {
        this.plugin = plugin;
        this.thread = new TablistThread();

        this.thread.start();

        this.plugin.registerListener(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                Player player = event.getPlayer();
                init(player);
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                remove(event.getPlayer());
            }
        });
    }


    public void init(Player player) {
        playerTabs.putIfAbsent(player.getUniqueId(), new Tablist(player));
        setup(player);
    }

    public void shutdown() {
        this.thread.stop();
    }

    public void setup(Player player) {
        getPlayerTab(player).setup();
    }

    public Tablist getPlayerTab(Player player) {
        return this.playerTabs.get(player.getUniqueId());
    }

    public void remove(Player player) {
        this.playerTabs.remove(player.getUniqueId());
    }
}

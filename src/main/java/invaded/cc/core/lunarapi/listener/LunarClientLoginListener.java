package invaded.cc.core.lunarapi.listener;

import invaded.cc.core.Spotify;
import invaded.cc.core.lunarapi.LunarAPIHandler;
import invaded.cc.core.lunarapi.event.LCPlayerRegisterEvent;
import invaded.cc.core.lunarapi.nethandler.client.LCPacketUpdateWorld;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

@RequiredArgsConstructor
public class LunarClientLoginListener implements Listener {

    private final Spotify plugin;
    private final LunarAPIHandler lunarClientAPI;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!lunarClientAPI.isRunningLunarClient(player)) {
                lunarClientAPI.failPlayerRegister(player);
            }
        }, 2 * 20L);
    }

    @EventHandler
    public void onRegister(PlayerRegisterChannelEvent event) {
        if (!lunarClientAPI.getChannels().contains(event.getChannel())) return;
        Player player = event.getPlayer();

        lunarClientAPI.registerPlayer(player);

        plugin.getServer().getPluginManager().callEvent(new LCPlayerRegisterEvent(event.getPlayer()));
        updateWorld(event.getPlayer());
    }

    @EventHandler
    public void onUnregister(PlayerUnregisterChannelEvent event) {
        if (lunarClientAPI.getChannels().contains(event.getChannel())) {
            lunarClientAPI.unregisterPlayer(event.getPlayer(), false);
        }
    }

    @EventHandler
    public void onUnregister(PlayerQuitEvent event) {
        lunarClientAPI.unregisterPlayer(event.getPlayer(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        updateWorld(event.getPlayer());
    }

    private void updateWorld(Player player) {
        String worldIdentifier = lunarClientAPI.getWorldIdentifier(player.getWorld());

        lunarClientAPI.sendPacket(player, new LCPacketUpdateWorld(worldIdentifier));
    }
}
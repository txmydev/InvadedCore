package invaded.cc.core.listener;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddressListener implements Listener {

    @Getter
    private final static Map<UUID, InetAddress> addresses = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        addresses.put(event.getUniqueId(), event.getAddress());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncLoginCancel(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult().name().contains("KICK")) {
            addresses.remove(event.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoginCancel(PlayerLoginEvent event) {
        if (event.getResult().name().contains("KICK")) {
            addresses.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoginCancel(PlayerQuitEvent event) {
        addresses.remove(event.getPlayer().getUniqueId());
    }

}

package invaded.cc.core.nametag;

import invaded.cc.core.Spotify;
import invaded.cc.core.nametag.impl.DefaultNametagProvider;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface NametagProvider {

    DefaultNametagProvider DEFAULT_NAMETAG_PROVIDER = new DefaultNametagProvider(JavaPlugin.getPlugin(Spotify.class));

    default boolean isAutoUpdate() {
        return false;
    }

    default int getUpdateInterval() {
        return 5;
    }

    Nametag getNametag(Player player, Player target);
}
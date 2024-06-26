package invaded.cc.core.tablist.client;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import us.myles.ViaVersion.api.Via;

public class ClientVersionUtil {

    /**
     * Get the protocol version of the client.
     * <br>
     * Had to be made because 1.8+ doesn't have a NetworkManager#getVersion method, which is required for legacy support on tab.
     *
     * @param player the player to get the version of
     * @return the version, or -1 if none of the plugins are supported.
     */
    public static int getProtocolVersion(Player player) {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        if(pluginManager.getPlugin("ViaVersion") != null) {
            return Via.getAPI().getPlayerVersion(player.getUniqueId());
        }

        return -1;
    }

}

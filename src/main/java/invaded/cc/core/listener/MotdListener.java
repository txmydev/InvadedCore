package invaded.cc.core.listener;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.ConfigFile;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.List;
import java.util.stream.Collectors;

public class MotdListener implements Listener {

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        ConfigFile config = Spotify.getInstance().getMainConfig();

        if(!config.get().contains("motd")) return;

        List<String> list = config.get().getStringList("motd").stream().map(Color::translate).collect(Collectors.toList());
        event.setMotd(Strings.join(list, "\n" + ChatColor.RESET.toString()));
    }

}

package invaded.cc.core.listener;

import invaded.cc.core.Spotify;
import invaded.cc.core.network.server.ServerHandler;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

@RequiredArgsConstructor
public class ServerListener implements Listener {

    private final Spotify plugin;

}

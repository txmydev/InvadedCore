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

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        ServerHandler serverHandler = plugin.getServerHandler();
        if(serverHandler.isMaintenance()) {
            event.setCustomProtocol(true);
            event.setProtocol(180);
            event.setProtocolMessage("Maintenance");
        } else if(serverHandler.isTesting()) {
            event.setCustomProtocol(true);
            event.setProtocol(180);
            event.setProtocolMessage("Testing");
        }
    }

}

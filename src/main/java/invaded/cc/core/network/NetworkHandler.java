package invaded.cc.core.network;

import invaded.cc.core.Spotify;
import invaded.cc.core.network.connection.BungeeConnectionHandler;
import invaded.cc.core.network.connection.JedisConnectionHandler;
import invaded.cc.core.network.packet.*;
import lombok.Getter;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.HashMap;
import java.util.Map;

@Getter
public class NetworkHandler {

    private final Map<String, PacketListener> packetListenerMap;
    private final ConnectionHandler connectionHandler;
    private final boolean networkMode;

    public NetworkHandler(Spotify plugin) {
        this.packetListenerMap = new HashMap<>();
        this.connectionHandler = Spotify.getInstance().getRedisDatabase().isRedisMode() ? new JedisConnectionHandler() : new BungeeConnectionHandler();
        this.networkMode = plugin.getMainConfig().get().getBoolean("network-mode", false);

        this.registerPacketReaders();
        this.registerChannels();
    }

    public void shutdown() {
        this.connectionHandler.close();
    }

    private void registerChannels() {
        if(connectionHandler instanceof BungeeConnectionHandler) {
            Spotify.getInstance().getServer().getMessenger().registerIncomingPluginChannel(Spotify.getInstance(), "invaded-network", (PluginMessageListener) connectionHandler);
            Spotify.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(Spotify.getInstance(), "invaded-network");
        }
    }

    private void registerPacketReaders() {
        packetListenerMap.put("packet-staff-join", new PacketStaffJoin.Listener());
        packetListenerMap.put("packet-staff-leave", new PacketStaffLeave.Listener());
        packetListenerMap.put("packet-staffchat", new PacketStaffChat.Listener());
        packetListenerMap.put("packet-staff-switch", new PacketStaffSwitch());
        packetListenerMap.put("packet-server-information", new PacketServerInformation.Listener());
        packetListenerMap.put("packet-request-help", new PacketRequestHelp.Listener());
        packetListenerMap.put("packet-report-player", new PacketReportPlayer.Listener());
    }

    public void sendPacket(SpotifyPacket packet) {

        connectionHandler.sendPacket(packet);
    }
}

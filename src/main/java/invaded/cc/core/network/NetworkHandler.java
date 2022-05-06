package invaded.cc.core.network;

import invaded.cc.core.Spotify;
import invaded.cc.core.network.connection.BungeeConnectionHandler;
import invaded.cc.core.network.connection.JedisConnectionHandler;
import invaded.cc.core.network.packet.*;
import lombok.Getter;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.*;

@Getter
public class NetworkHandler {

    private final Set<PacketListener> packetListenerSet;
    private final ConnectionHandler connectionHandler;
    private final boolean networkMode;

    public NetworkHandler(Spotify plugin) {
        this.connectionHandler = Spotify.getInstance().getRedisDatabase().isRedisMode() ? new JedisConnectionHandler() : new BungeeConnectionHandler();
        this.networkMode = plugin.getMainConfig().get().getBoolean("network-mode", false);
        this.packetListenerSet = new HashSet<>();

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
        packetListenerSet.addAll(
                Arrays.asList(new PacketStaffJoin.Listener("packet-staff-join"),
                        new PacketStaffLeave.Listener("packet-staff-leave"),
                        new PacketStaffChat.Listener("packet-staffchat"),
                        new PacketStaffSwitch("packet-staff-switch"),
                        new PacketServerInformation.Listener("packet-server-information"),
                        new PacketRequestHelp.Listener("packet-request-help"),
                        new PacketReportPlayer.Listener("packet-report-player"))
        );
    }

    public void sendPacket(SpotifyPacket packet) {
        connectionHandler.sendPacket(packet);
    }
}

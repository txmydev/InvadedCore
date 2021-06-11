package invaded.cc.core.network.server;

import invaded.cc.core.Spotify;
import invaded.cc.core.network.packet.PacketServerInformation;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerHeartbeatTask extends BukkitRunnable {

    @Override
    public void run() {
        Spotify.getInstance().getNetworkHandler().getConnectionHandler().sendPacket(PacketServerInformation.createPacket());
    }
}

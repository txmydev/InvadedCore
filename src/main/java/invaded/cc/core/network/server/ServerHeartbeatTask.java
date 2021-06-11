package invaded.cc.core.network.server;

import invaded.cc.core.Spotify;
import invaded.cc.core.network.packet.PacketServerInformation;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class ServerHeartbeatTask extends BukkitRunnable {

    public static void start() {
        new ServerHeartbeatTask().runTaskTimerAsynchronously(Spotify.getInstance(), 10L, 5L);
    }

    @Override
    public void run() {
        Spotify.getInstance().getNetworkHandler().sendPacket(PacketServerInformation.createPacket());

        Iterator<Server> iterator = Spotify.getInstance().getServerHandler().getServerMap().values().iterator();
        while(iterator.hasNext()) {
            Server server = iterator.next();
            if(!server.isOnline()) Spotify.getInstance().getServerHandler().deleteServer(server.getName());
            iterator.remove();
        }
    }
}

package invaded.cc.core.network.packet;

import invaded.cc.core.Spotify;
import invaded.cc.core.network.PacketListener;
import invaded.cc.core.network.SpotifyPacket;
import invaded.cc.core.network.server.Server;
import invaded.cc.core.network.server.ServerHandler;
import invaded.cc.core.util.json.JsonChain;
import lombok.Getter;
import invaded.cc.common.library.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

@Getter
public class PacketServerInformation extends SpotifyPacket {

    private final String name;
    private final boolean testing;
    private final boolean maintenance;
    private final int online;
    private final long lastUpdate;
    private final String extraInfo;

    public PacketServerInformation(String name, boolean testing, boolean maintenance, int online, long lastUpdate, String extraInfo){
        super("packet-server-information");

        this.name = name;
        this.testing = testing;
        this.maintenance = maintenance;
        this.online = online;
        this.lastUpdate = lastUpdate;
        this.extraInfo = extraInfo;
    }

    public static SpotifyPacket createPacket() {
        ServerHandler serverHandler = Spotify.getInstance().getServerHandler();
        return new PacketServerInformation(Spotify.SERVER_NAME, serverHandler.isTesting(), serverHandler.isMaintenance(), Bukkit.getOnlinePlayers().size(), System.currentTimeMillis(), serverHandler.getExtraInfo());
    }

    @Override
    public JsonObject toJson() {
        return new JsonChain()
                .addProperty("packet-id", getPacketId())
                .addProperty("name", name)
                .addProperty("testing", testing)
                .addProperty("maintenance", maintenance)
                .addProperty("online", online)
                .addProperty("lastUpdate", lastUpdate)
                .addProperty("extraInfo", extraInfo)
                .get();
    }

    @Getter
    public static class Listener extends PacketListener {


        public Listener(String packet) {
            super(packet);
        }

        @Override
        public void onReceivePacket(JsonObject packet) {
            String name = packet.get("name").getAsString();
            boolean testing = packet.get("testing").getAsBoolean();
            boolean maintenance = packet.get("maintenance").getAsBoolean();
            int online = packet.get("online").getAsInt();
            long lastUpdate = packet.get("lastUpdate").getAsLong();

            ServerHandler serverHandler = Spotify.getInstance().getServerHandler();
            Server server = serverHandler.getServer(name);
            if(server != null) server.setRecentlyCreated(false);
            if(server == null) server = serverHandler.createServer(name);
            server.setLastUpdate(System.currentTimeMillis());
            server.setOnline(online);

            if(server.isTesting() != testing) serverHandler.setTestingMode(name, testing);
            if(server.isMaintenance() != maintenance) serverHandler.setMaintenanceMode(name, maintenance);

            server.setExtraInfo(packet.get("extraInfo").getAsString());
        }
    }
}

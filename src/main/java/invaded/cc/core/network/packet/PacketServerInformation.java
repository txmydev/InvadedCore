package invaded.cc.core.network.packet;

import invaded.cc.core.Spotify;
import invaded.cc.core.network.PacketListener;
import invaded.cc.core.network.SpotifyPacket;
import invaded.cc.core.network.server.Server;
import invaded.cc.core.network.server.ServerHandler;
import invaded.cc.core.util.json.JsonChain;
import lombok.Getter;
import net.minecraft.util.com.google.gson.JsonObject;

@Getter
public class PacketServerInformation extends SpotifyPacket {

    private final String name;
    private final boolean testing;
    private final boolean maintenance;
    private final int online;
    private final long lastUpdate;

    public PacketServerInformation(String name, boolean testing, boolean maintenance, int online, long lastUpdate){
        super("packet-server-information");

        this.name = name;
        this.testing = testing;
        this.maintenance = maintenance;
        this.online = online;
        this.lastUpdate = lastUpdate;
    }

    public static SpotifyPacket createPacket() {
        Server server = Spotify.getInstance().getServerHandler().getServer(Spotify.SERVER_NAME);
        if(server == null) server = new Server(Spotify.SERVER_NAME);
        return new PacketServerInformation(server.getName(), server.isTesting(), server.isMaintenance(), server.getOnline(), System.currentTimeMillis());
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
                .get();
    }

    public static class Listener extends PacketListener {

        @Override
        public void onReceivePacket(JsonObject packet) {
            String name = packet.get("name").getAsString();
            boolean testing = packet.get("testing").getAsBoolean();
            boolean maintenance = packet.get("maintenance").getAsBoolean();
            int online = packet.get("online").getAsInt();
            long lastUpdate = packet.get("lastUpdate").getAsLong();

            ServerHandler serverHandler = Spotify.getInstance().getServerHandler();
            Server server = serverHandler.getServer(name);
            if(server == null) server = serverHandler.createServer(name);
            if(server.isTesting() != testing) serverHandler.setTestingMode(name, testing);
            if(server.isMaintenance() != maintenance) serverHandler.setMaintenanceMode(name, maintenance);
            server.setOnline(online);
            server.setLastUpdate(lastUpdate);
        }
    }
}

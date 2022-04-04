package invaded.cc.core.network.packet;

import invaded.cc.common.library.gson.JsonObject;
import invaded.cc.core.network.PacketListener;
import invaded.cc.core.network.SpotifyPacket;
import invaded.cc.core.util.json.JsonChain;

public class PacketJoinServer extends SpotifyPacket {

    private final String player, targetServer;

    public PacketJoinServer(String player, String targetServer) {
        super("packet-join-server");

        this.player = player;
        this.targetServer = targetServer;
    }

    @Override
    public JsonObject toJson() {
        return new JsonChain()
                .addProperty("packet-id", getPacketId())
                .addProperty("player", player)
                .addProperty("targetServer", targetServer)
                .get();
    }

    public static class Listener extends PacketListener {

        @Override
        public void onReceivePacket(JsonObject packet) {
        }

    }

}

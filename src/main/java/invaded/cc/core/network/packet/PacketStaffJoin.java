package invaded.cc.core.network.packet;

import invaded.cc.core.Spotify;
import invaded.cc.core.network.PacketListener;
import invaded.cc.core.network.SpotifyPacket;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.json.JsonChain;
import invaded.cc.core.util.perms.PermLevel;
import lombok.Getter;
import invaded.cc.common.library.gson.JsonObject;

@Getter
public class PacketStaffJoin extends SpotifyPacket {

    private final String name;
    private final String server;

    public PacketStaffJoin(String name,String server) {
        super("packet-staff-join");

        this.name = name;
        this.server = server;
    }

    @Override
    public JsonObject toJson() {
        return new JsonChain()
                .addProperty("packet-id", getPacketId())
                .addProperty("name", name)
                .addProperty("server", server)
                .get();
    }

    public static class Listener extends PacketListener {

        public Listener(String packet) {
            super(packet);
        }

        @Override
        public void onReceivePacket(JsonObject packet) {
            String name = packet.get("name").getAsString();
            String server = packet.get("server").getAsString();

            Common.broadcastMessage(PermLevel.STAFF, "&9[Staff] " + name + " &ajoined &bthe network. &7(To " + server + ")");
        }

    }
}

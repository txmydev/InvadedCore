package invaded.cc.core.network.packet;

import invaded.cc.core.network.SpotifyPacket;
import invaded.cc.core.util.json.JsonChain;

public class PacketProfileInformation extends SpotifyPacket {

    private final String id, name, coloredName, rank, server;
    private final boolean staff;

    public PacketProfileInformation(String id, String name, String coloredName, String rank, String server, boolean staff) {
        super("packet-profile-information");

        this.id = id;
        this.name = name;
        this.coloredName = coloredName;
        this.rank = rank;
        this.server = server;
        this.staff = staff;
    }

    @Override
    public String toJson() {
        return new JsonChain().addProperty("packet-id", getPacketId())
                .addProperty("id", id)
                .addProperty("name", name)
                .addProperty("coloredName", coloredName)
                .addProperty("rank", rank)
                .addProperty("staff", staff)
                .addProperty("server", server)
                .get()
                .toString();
    }
}

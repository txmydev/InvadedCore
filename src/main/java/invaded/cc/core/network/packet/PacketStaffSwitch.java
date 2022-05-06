package invaded.cc.core.network.packet;

import invaded.cc.core.Spotify;
import invaded.cc.core.network.PacketListener;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.perms.PermLevel;
import invaded.cc.common.library.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class PacketStaffSwitch extends PacketListener {


    public PacketStaffSwitch(String packet) {
        super(packet);
    }

    @Override
    public void onReceivePacket(JsonObject packet) {
        String name = packet.get("name").getAsString();
        String from = packet.get("from").getAsString();
        String to = packet.get("to").getAsString();

        if(from.equals(Spotify.SERVER_NAME)) Common.broadcastMessage(PermLevel.STAFF, "&9[Staff] "+ name + " &bleft your server. &7(To " + to + ")");
        else if(to.equals(Spotify.SERVER_NAME)) Common.broadcastMessage(PermLevel.STAFF, "&9[Staff] " + name + " &bjoined your server. &7(From " + from +")");
        else Common.broadcastMessage(PermLevel.STAFF, "&9[Staff] " + name + " &bjoined to " + to + " &bfrom " + from + ".");
    }
}

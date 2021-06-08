package invaded.cc.core.network.packet;

import invaded.cc.core.network.PacketListener;
import invaded.cc.core.network.SpotifyPacket;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.json.JsonChain;
import invaded.cc.core.util.perms.PermLevel;
import lombok.Getter;
import net.minecraft.util.com.google.gson.JsonObject;
import org.bukkit.potion.PotionEffectType;

@Getter
public class PacketStaffChat extends SpotifyPacket {

    private final String name, server, message;

    public PacketStaffChat(String name, String server, String message) {
        super("packet-staffchat");

        this.name = name;
        this.message = message;
        this.server = server;
    }

    @Override
    public String toJson() {
        return new JsonChain()
                .addProperty("packet-id", getPacketId())
                .addProperty("name", name)
                .addProperty("message", message)
                .addProperty("server", server)
                .get().toString();
    }

    public static class Listener extends PacketListener {

        @Override
        public void onReceivePacket(JsonObject packet) {
            String name = packet.get("name").getAsString();
            String message = packet.get("message").getAsString();
            String server = packet.get("server").getAsString();

            Common.broadcastMessage(PermLevel.STAFF, "&7[" + server + "&7] " + name + "&7: &d" + message);
        }
    }
}

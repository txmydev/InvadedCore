package invaded.cc.core.network;

import net.minecraft.util.com.google.gson.JsonObject;

public abstract class PacketListener {

    public abstract void onReceivePacket(JsonObject packet);
    public void onSendPacket(SpotifyPacket packet) {

    }
}

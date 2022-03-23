package invaded.cc.core.network;

import invaded.cc.common.library.gson.JsonObject;

public abstract class PacketListener {

    public abstract void onReceivePacket(JsonObject packet);
    public void onSendPacket(SpotifyPacket packet) {

    }
}

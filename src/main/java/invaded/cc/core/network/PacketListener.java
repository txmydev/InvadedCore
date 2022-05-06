package invaded.cc.core.network;

import invaded.cc.common.library.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public abstract class PacketListener {

    private final String packet;

    public abstract void onReceivePacket(JsonObject packet);
    public void onSendPacket(SpotifyPacket packet) {

    }
}

package invaded.cc.core.network;

import invaded.cc.core.Spotify;
import lombok.Getter;
import invaded.cc.common.library.gson.JsonObject;

@Getter
public abstract class SpotifyPacket {

    private final String packetId;

    public SpotifyPacket(String packetId) {
        this.packetId = packetId;
    }

    public abstract JsonObject toJson();


}

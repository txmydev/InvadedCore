package invaded.cc.core.network;

import invaded.cc.core.Spotify;
import lombok.Getter;

@Getter
public abstract class SpotifyPacket {

    private final String packetId;

    public SpotifyPacket(String packetId) {
        this.packetId = packetId;
    }

    public abstract String toJson();


}

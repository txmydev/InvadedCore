package invaded.cc.core.network;

import invaded.cc.common.library.gson.JsonObject;

public abstract class ConnectionHandler {

    public abstract void sendPacket(SpotifyPacket packet);
    public abstract void receivePacket(JsonObject jsonObject);

    public void close() {
    }

}

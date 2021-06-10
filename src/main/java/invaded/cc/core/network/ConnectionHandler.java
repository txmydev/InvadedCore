package invaded.cc.core.network;

import net.minecraft.util.com.google.gson.JsonObject;

public abstract class ConnectionHandler {

    public abstract void sendPacket(SpotifyPacket packet);
    public abstract void receivePacket(JsonObject jsonObject);

    public void close() {
    }

}

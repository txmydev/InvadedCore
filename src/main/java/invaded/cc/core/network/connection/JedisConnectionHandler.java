package invaded.cc.core.network.connection;

import invaded.cc.core.Spotify;
import invaded.cc.core.network.ConnectionHandler;
import invaded.cc.core.network.PacketListener;
import invaded.cc.core.network.SpotifyPacket;
import invaded.cc.core.util.jedis.JedisConfiguration;
import invaded.cc.core.util.jedis.JedisPublisher;
import invaded.cc.core.util.jedis.JedisSubscriber;
import lombok.Getter;
import invaded.cc.common.library.gson.JsonObject;

import java.util.Map;

@Getter
public class JedisConnectionHandler extends ConnectionHandler {

    private JedisSubscriber subscriber;
    private JedisPublisher publisher;

    public JedisConnectionHandler() {
        JedisConfiguration config = Spotify.getInstance().getRedisDatabase().getConfig();

        subscriber = new JedisSubscriber(config, "invaded-packet", (channel, jsonObject) -> {
            if (!channel.equals("invaded-packet")) return;
            if (!jsonObject.has("packet-id")) return;
            this.receivePacket(jsonObject);
        });

        this.publisher = new JedisPublisher(config, "invaded-packet");
    }

    @Override
    public void sendPacket(SpotifyPacket packet) {
        this.publisher.write(packet.toJson());
    }

    @Override
    public void receivePacket(JsonObject jsonObject) {
        String packetId = jsonObject.get("packet-id").getAsString();
        if(Spotify.getInstance().getNetworkHandler() == null) return;

        for (PacketListener listener : Spotify.getInstance().getNetworkHandler().getPacketListenerSet()) {
            if(listener.getPacket().equalsIgnoreCase(packetId)) {
                listener.onReceivePacket(jsonObject);
                break;
            }
        }

    }

    @Override
    public void close() {
        subscriber.stop();
    }
}

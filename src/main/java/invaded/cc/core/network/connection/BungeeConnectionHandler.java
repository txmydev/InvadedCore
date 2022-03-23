package invaded.cc.core.network.connection;

import invaded.cc.core.Spotify;
import invaded.cc.core.network.ConnectionHandler;
import invaded.cc.core.network.PacketListener;
import invaded.cc.core.network.SpotifyPacket;
import net.minecraft.util.com.google.common.io.ByteArrayDataInput;
import net.minecraft.util.com.google.common.io.ByteArrayDataOutput;
import net.minecraft.util.com.google.common.io.ByteStreams;
import invaded.cc.common.library.gson.JsonObject;
import invaded.cc.common.library.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Map;

public class BungeeConnectionHandler extends ConnectionHandler implements PluginMessageListener {

    @Override
    public void sendPacket(SpotifyPacket packet) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(packet.toJson().toString());
        byte[] data = output.toByteArray();

        Map<String, PacketListener> map = Spotify.getInstance().getNetworkHandler().getPacketListenerMap();

        if(map.containsKey(packet.getPacketId()))
            map.get(packet.getPacketId()).onSendPacket(packet);

        Bukkit.getOnlinePlayers().stream().findAny().ifPresent(player -> player.sendPluginMessage(Spotify.getInstance(), "invaded-network", data));
    }

    @Override
    public void receivePacket(JsonObject jsonObject) {
        String packetId = jsonObject.get("packet-id").getAsString();
        Map<String, PacketListener> map = Spotify.getInstance().getNetworkHandler().getPacketListenerMap();

        if(map.containsKey(packetId))
            map.get(packetId).onReceivePacket(jsonObject);
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if(!s.equals("invaded-network")) return;

        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        JsonObject jsonObject = new JsonParser().parse(input.readUTF()).getAsJsonObject();
        receivePacket(jsonObject);
    }
}

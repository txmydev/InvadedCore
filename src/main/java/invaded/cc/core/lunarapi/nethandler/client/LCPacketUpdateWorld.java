package invaded.cc.core.lunarapi.nethandler.client;

import invaded.cc.core.lunarapi.nethandler.ByteBufWrapper;
import invaded.cc.core.lunarapi.nethandler.LCPacket;
import invaded.cc.core.lunarapi.nethandler.shared.LCNetHandler;
import lombok.Getter;

import java.io.IOException;

public final class LCPacketUpdateWorld extends LCPacket {

    @Getter private String world;

    public LCPacketUpdateWorld() {}

    public LCPacketUpdateWorld(String world) {
        this.world = world;
    }

    @Override
    public void write(ByteBufWrapper buf) throws IOException {
        buf.writeString(world);
    }

    @Override
    public void read(ByteBufWrapper buf) throws IOException {
        this.world = buf.readString();
    }

    @Override
    public void process(LCNetHandler handler) {
        ((LCNetHandlerClient) handler).handleUpdateWorld(this);
    }

}
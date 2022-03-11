package invaded.cc.core.lunarapi.nethandler.client;

import invaded.cc.core.lunarapi.nethandler.ByteBufWrapper;
import invaded.cc.core.lunarapi.nethandler.LCPacket;
import invaded.cc.core.lunarapi.nethandler.shared.LCNetHandler;
import lombok.Getter;

import java.io.IOException;

public final class LCPacketServerUpdate extends LCPacket {

    @Getter private String server;

    public LCPacketServerUpdate() {}

    public LCPacketServerUpdate(String server) {
        this.server = server;
    }

    @Override
    public void write(ByteBufWrapper buf) throws IOException {
        buf.writeString(server);
    }

    @Override
    public void read(ByteBufWrapper buf) throws IOException {
        this.server = buf.readString();
    }

    @Override
    public void process(LCNetHandler handler) {
        ((LCNetHandlerClient) handler).handleServerUpdate(this);
    }

}
package invaded.cc.core.lunarapi.nethandler.client;

import invaded.cc.core.lunarapi.nethandler.ByteBufWrapper;
import invaded.cc.core.lunarapi.nethandler.LCPacket;
import invaded.cc.core.lunarapi.nethandler.server.LCNetHandlerServer;
import invaded.cc.core.lunarapi.nethandler.shared.LCNetHandler;
import lombok.Getter;

import java.io.IOException;
import java.util.UUID;

public final class LCPacketVoiceMute extends LCPacket {

    @Getter private UUID muting;

    @Getter private int volume;

    public LCPacketVoiceMute() {
    }

    public LCPacketVoiceMute(UUID muting) {
        this(muting, 0);
    }

    public LCPacketVoiceMute(UUID muting, int volume) {
        this.muting = muting;
        this.volume = volume;
    }

    @Override
    public void write(ByteBufWrapper b) throws IOException {
        b.writeUUID(muting);
        b.writeVarInt(volume);
    }

    @Override
    public void read(ByteBufWrapper b) throws IOException {
        this.muting = b.readUUID();
        this.volume = b.readVarInt();
    }

    @Override
    public void process(LCNetHandler handler) {
        ((LCNetHandlerServer) handler).handleVoiceMute(this);
    }
}

package invaded.cc.core.lunarapi.nethandler.client;

import invaded.cc.core.lunarapi.nethandler.ByteBufWrapper;
import invaded.cc.core.lunarapi.nethandler.LCPacket;
import invaded.cc.core.lunarapi.nethandler.shared.LCNetHandler;
import lombok.Getter;

import java.io.IOException;

public final class LCPacketCooldown extends LCPacket {

    @Getter private String message;
    @Getter private long durationMs;
    @Getter private int iconId;

    public LCPacketCooldown() {}

    public LCPacketCooldown(String message, long durationMs, int iconId) {
        this.message = message;
        this.durationMs = durationMs;
        this.iconId = iconId;
    }

    @Override
    public void write(ByteBufWrapper buf) throws IOException {
        buf.writeString(message);
        buf.buf().writeLong(durationMs);
        buf.buf().writeInt(iconId);
    }

    @Override
    public void read(ByteBufWrapper buf) throws IOException {
        message = buf.readString();
        durationMs = buf.buf().readLong();
        iconId = buf.buf().readInt();
    }

    @Override
    public void process(LCNetHandler handler) {
        ((LCNetHandlerClient) handler).handleCooldown(this);
    }

}
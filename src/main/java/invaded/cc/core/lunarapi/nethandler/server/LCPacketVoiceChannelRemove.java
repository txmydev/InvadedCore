package invaded.cc.core.lunarapi.nethandler.server;

import invaded.cc.core.lunarapi.nethandler.ByteBufWrapper;
import invaded.cc.core.lunarapi.nethandler.LCPacket;
import invaded.cc.core.lunarapi.nethandler.client.LCNetHandlerClient;
import invaded.cc.core.lunarapi.nethandler.shared.LCNetHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public final class LCPacketVoiceChannelRemove extends LCPacket {

    @Getter private UUID uuid;

    @Override
    public void write(ByteBufWrapper b) {
        b.writeUUID(uuid);
    }

    @Override
    public void read(ByteBufWrapper b) {
        this.uuid = b.readUUID();
    }

    @Override
    public void process(LCNetHandler handler) {
        ((LCNetHandlerClient) handler).handleVoiceChannelDelete(this);
    }
}

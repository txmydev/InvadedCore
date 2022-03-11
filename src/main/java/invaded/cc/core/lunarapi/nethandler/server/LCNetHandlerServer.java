package invaded.cc.core.lunarapi.nethandler.server;

import invaded.cc.core.lunarapi.nethandler.client.LCPacketClientVoice;
import invaded.cc.core.lunarapi.nethandler.client.LCPacketVoiceChannelSwitch;
import invaded.cc.core.lunarapi.nethandler.client.LCPacketVoiceMute;
import invaded.cc.core.lunarapi.nethandler.shared.LCNetHandler;

public interface LCNetHandlerServer extends LCNetHandler {

    void handleStaffModStatus(LCPacketStaffModStatus packet);
    void handleVoice(LCPacketClientVoice packet);
    void handleVoiceMute(LCPacketVoiceMute packet);
    void handleVoiceChannelSwitch(LCPacketVoiceChannelSwitch packet);
}